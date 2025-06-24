# WAITLISTED Status Implementation Guide

## 🎯 Overview
The WAITLISTED status is automatically assigned to users when they try to book a class that has reached its maximum capacity. This ensures fair access to classes through a First-In-First-Out (FIFO) queue system.

## 🔄 Waitlist Flow

### 1. **Getting WAITLISTED**
```java
// When a class is full, users are automatically waitlisted
if (currentBookedCount >= classSchedule.getMaxCapacity()) {
    int waitlistPosition = getNextWaitlistPosition(classSchedule);
    booking.setStatus(BookingStatus.WAITLISTED);
    booking.setWaitlistPosition(waitlistPosition);
    // NO CREDITS ARE DEDUCTED for waitlisted users
}
```

### 2. **Key Characteristics of WAITLISTED Status**
- ✅ **No Credit Deduction**: Credits are NOT deducted when waitlisted
- ✅ **Position Tracking**: Each waitlisted user gets a position number
- ✅ **FIFO Promotion**: First waitlisted user gets promoted when a spot opens
- ✅ **Automatic Promotion**: System automatically promotes users when someone cancels
- ✅ **Package Validation**: System checks if user's package is still valid before promotion

### 3. **Promotion from Waitlist**
```java
private void promoteFromWaitlist(ClassSchedule classSchedule) {
    // Find the next person in line (lowest waitlist position)
    Optional<ClassBooking> nextWaitlisted = classBookingRepository
        .findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
            classSchedule, BookingStatus.WAITLISTED);

    if (nextWaitlisted.isPresent()) {
        ClassBooking waitlistedBooking = nextWaitlisted.get();
        
        // Validate user's package is still active
        UserPackage userPackage = waitlistedBooking.getUserPackage();
        if (userPackage.isActive() && userPackage.getRemainingCredits() >= classSchedule.getRequiredCredits()) {
            // Promote to BOOKED status
            waitlistedBooking.setStatus(BookingStatus.BOOKED);
            waitlistedBooking.setWaitlistPosition(null);
            
            // NOW deduct credits (only when promoted)
            deductCreditsFromPackage(userPackage, classSchedule.getRequiredCredits());
        }
    }
}
```

## 📋 Testing WAITLISTED Status

### Test Scenario 1: Fill a Class to Capacity
```bash
# 1. Get a class with small capacity (e.g., max_capacity = 2)
GET /api/schedules?country=SG

# 2. Book the class with first user until it's full
POST /api/bookings/{classScheduleId}
# Response: status = "BOOKED"

# 3. Book with second user
POST /api/bookings/{classScheduleId}  
# Response: status = "BOOKED"

# 4. Book with third user (should be waitlisted)
POST /api/bookings/{classScheduleId}
# Response: status = "WAITLISTED", waitlistPosition = 1
```

### Test Scenario 2: Waitlist Promotion
```bash
# 1. Cancel a booked user's booking
DELETE /api/bookings/{bookingId}

# 2. Check waitlisted user's booking status
GET /api/bookings
# The waitlisted user should now be "BOOKED" and credits deducted
```

## 🎮 Postman Testing Steps

### Step 1: Create Test Class with Small Capacity
```sql
-- Add a test class with capacity of 2
INSERT INTO class_schedules (class_name, instructor, start_time, end_time, max_capacity, required_credits, country, location, status) 
VALUES ('Test Yoga', 'Test Instructor', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 1 HOUR), 2, 1, 'SG', 'Test Studio', 'SCHEDULED');
```

### Step 2: Test Waitlist Flow
1. **Login as User 1** → Book the test class → Should get "BOOKED"
2. **Login as User 2** → Book the test class → Should get "BOOKED" 
3. **Login as User 3** → Book the test class → Should get "WAITLISTED" with position 1
4. **Login as User 4** → Book the test class → Should get "WAITLISTED" with position 2

### Step 3: Test Promotion
1. **User 1 cancels** → User 3 should automatically become "BOOKED"
2. **Check User 3's bookings** → Status should be "BOOKED", waitlistPosition should be null
3. **Check User 4's bookings** → Should still be "WAITLISTED" but position should be 1 (moved up)

## 🔍 API Response Examples

### Waitlisted Booking Response
```json
{
  "id": 123,
  "className": "Morning Yoga SG",
  "instructor": "Sarah Lee",
  "startTime": "2025-01-26T08:00:00",
  "endTime": "2025-01-26T09:00:00",
  "location": "Studio A",
  "status": "WAITLISTED",
  "bookingTime": "2025-01-25T10:30:00",
  "cancellationTime": null,
  "checkInTime": null,
  "waitlistPosition": 1
}
```

### Promoted Booking Response
```json
{
  "id": 123,
  "className": "Morning Yoga SG",
  "instructor": "Sarah Lee",
  "startTime": "2025-01-26T08:00:00",
  "endTime": "2025-01-26T09:00:00",
  "location": "Studio A",
  "status": "BOOKED",
  "bookingTime": "2025-01-25T10:30:00",
  "cancellationTime": null,
  "checkInTime": null,
  "waitlistPosition": null
}
```

## ⚡ Key Business Rules

### Credits and Waitlist
- **WAITLISTED**: No credits deducted
- **PROMOTED to BOOKED**: Credits deducted at promotion time
- **CANCELLED from WAITLIST**: No credit refund needed (none were deducted)

### Automatic Processes
- **Class Completion**: All waitlisted users are automatically cancelled
- **Package Expiry**: Waitlisted users with expired packages are cancelled
- **Position Updates**: When someone leaves waitlist, all positions are updated

### Validation Rules
- Users can be waitlisted even if they don't have enough credits initially
- Credits are only checked and deducted when promoted to BOOKED
- Package validity is checked at promotion time, not at waitlist time

## 🚨 Error Scenarios

### Invalid Package at Promotion
```java
// If user's package expires or runs out of credits while waitlisted
if (!userPackage.isActive() || userPackage.getRemainingCredits() < requiredCredits) {
    // Cancel their waitlist booking
    waitlistedBooking.setStatus(BookingStatus.CANCELLED);
    // Try to promote the next person in line
    promoteFromWaitlist(classSchedule);
}
```

### Multiple Concurrent Cancellations
- Redis locks prevent race conditions during promotion
- Only one waitlisted user can be promoted at a time
- Positions are updated atomically

## 📊 Monitoring Waitlists

### Check Waitlist Status
```bash
# Get class schedule with waitlist count
GET /api/schedules?country=SG
# Response includes waitlistCount field

# Get user's waitlisted bookings
GET /api/bookings
# Filter by status = "WAITLISTED"
```

### Admin Queries (Database)
```sql
-- Find classes with high waitlist counts
SELECT cs.class_name, COUNT(cb.id) as waitlist_count 
FROM class_schedules cs 
JOIN class_bookings cb ON cs.id = cb.class_schedule_id 
WHERE cb.status = 'WAITLISTED' 
GROUP BY cs.id 
HAVING waitlist_count > 5;

-- Find users stuck in waitlist for too long
SELECT u.username, cs.class_name, cb.booking_time, cb.waitlist_position
FROM class_bookings cb
JOIN users u ON cb.user_id = u.id
JOIN class_schedules cs ON cb.class_schedule_id = cs.id
WHERE cb.status = 'WAITLISTED' 
AND cb.booking_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
```

This comprehensive waitlist system ensures fair access to classes while maintaining data integrity and providing a smooth user experience.