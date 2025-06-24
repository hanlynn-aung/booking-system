package com.app.booking.serviceImpl;

import com.app.booking.common.constant.BookingStatus;
import com.app.booking.common.constant.ClassStatus;
import com.app.booking.common.constant.UserPackageStatus;
import com.app.booking.controller.response.BookingResponse;
import com.app.booking.controller.response.ClassScheduleResponse;
import com.app.booking.entity.ClassBooking;
import com.app.booking.entity.ClassSchedule;
import com.app.booking.entity.User;
import com.app.booking.entity.UserPackage;
import com.app.booking.repository.ClassBookingRepository;
import com.app.booking.repository.ClassScheduleRepository;
import com.app.booking.repository.UserPackageRepository;
import com.app.booking.common.exception.BadRequestException;
import com.app.booking.common.exception.ResourceNotFoundException;
import com.app.booking.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookService {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${app.booking.cancellation-hours:4}")
    private int cancellationHours;

    @Value("${app.booking.checkin-before-minutes:15}")
    private int checkinBeforeMinutes;

    @Value("${app.booking.checkin-after-minutes:30}")
    private int checkinAfterMinutes;

    @Override
    public List<ClassScheduleResponse> getClassSchedules(String country) {
        List<ClassSchedule> schedules = classScheduleRepository.findByCountryAndStatusOrderByStartTime(
                country, ClassStatus.SCHEDULED);
        
        return schedules.stream()
                .map(this::convertToClassScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse bookClass(String username, Long classScheduleId) {
        User user = userService.findByUsername(username);
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Class schedule not found"));

        // Validate class is bookable
        validateClassBookable(classSchedule);

        // Check for time conflicts with user's existing bookings
        validateNoTimeConflict(user, classSchedule);

        // Find suitable user package for this country
        UserPackage userPackage = findSuitableUserPackage(user, classSchedule);
        if (userPackage == null) {
            throw new BadRequestException("No suitable active package found for " + classSchedule.getCountry() + 
                " with sufficient credits (" + classSchedule.getRequiredCredits() + " required)");
        }

        // Use Redis distributed lock for concurrent booking control
        String lockKey = "class_booking_lock:" + classScheduleId;
        String lockValue = "lock_" + username + "_" + System.currentTimeMillis();
        
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new BadRequestException("Class booking is temporarily unavailable due to high demand. Please try again in a moment.");
        }

        try {
            // Re-check capacity within the lock to prevent race conditions
            long currentBookedCount = classBookingRepository.countByClassScheduleAndStatus(
                    classSchedule, BookingStatus.BOOKED);

            ClassBooking booking = ClassBooking.builder()
                    .user(user)
                    .classSchedule(classSchedule)
                    .userPackage(userPackage)
                    .bookingTime(LocalDateTime.now())
                    .build();

            if (currentBookedCount >= classSchedule.getMaxCapacity()) {
                // Class is full - add to waitlist
                int waitlistPosition = getNextWaitlistPosition(classSchedule);
                booking.setStatus(BookingStatus.WAITLISTED);
                booking.setWaitlistPosition(waitlistPosition);
                
                log.info("User {} added to waitlist for class {} at position {}", 
                    username, classSchedule.getClassName(), waitlistPosition);
            } else {
                // Class has available spots - confirm booking and deduct credits
                booking.setStatus(BookingStatus.BOOKED);
                deductCreditsFromPackage(userPackage, classSchedule.getRequiredCredits());
                
                log.info("User {} successfully booked class {} with {} credits deducted", 
                    username, classSchedule.getClassName(), classSchedule.getRequiredCredits());
            }

            ClassBooking savedBooking = classBookingRepository.save(booking);
            return convertToBookingResponse(savedBooking);

        } finally {
            // Always release the lock
            String currentLockValue = (String) redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    @Override
    @Transactional
    public void cancelBooking(String username, Long bookingId) {
        User user = userService.findByUsername(username);
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Validate ownership
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        // Validate booking status
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Cannot cancel a booking that has been checked in");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = booking.getClassSchedule().getStartTime();
        boolean isEarlyCancellation = now.isBefore(classStartTime.minusHours(cancellationHours));

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationTime(now);

        // Handle credit refund and waitlist promotion for confirmed bookings
        if (booking.getStatus() == BookingStatus.BOOKED) {
            if (isEarlyCancellation) {
                // Refund credits for early cancellation
                refundCreditsToPackage(booking.getUserPackage(), booking.getClassSchedule().getRequiredCredits());
                log.info("Credits refunded for early cancellation by user {}", username);
            } else {
                log.info("No credit refund for late cancellation by user {} (within {} hours)", username, cancellationHours);
            }

            // Promote next waitlisted user to booked status
            promoteFromWaitlist(booking.getClassSchedule());
        } else if (booking.getStatus() == BookingStatus.WAITLISTED) {
            // Update waitlist positions for remaining users
            updateWaitlistPositions(booking.getClassSchedule(), booking.getWaitlistPosition());
        }

        classBookingRepository.save(booking);
        log.info("Booking {} cancelled successfully by user {}", bookingId, username);
    }

    @Override
    @Transactional
    public void checkInToClass(String username, Long bookingId) {
        User user = userService.findByUsername(username);
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Validate ownership
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only check in to your own bookings");
        }

        // Validate booking status
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new BadRequestException("Only confirmed bookings can be checked in. Current status: " + booking.getStatus());
        }

        if (booking.getCheckInTime() != null) {
            throw new BadRequestException("You have already checked in to this class");
        }

        // Validate check-in time window
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = booking.getClassSchedule().getStartTime();
        LocalDateTime checkinWindowStart = classStartTime.minusMinutes(checkinBeforeMinutes);
        LocalDateTime checkinWindowEnd = classStartTime.plusMinutes(checkinAfterMinutes);
        
        if (now.isBefore(checkinWindowStart)) {
            throw new BadRequestException(String.format("Check-in is not yet available. You can check in starting %d minutes before class start time", checkinBeforeMinutes));
        }
        
        if (now.isAfter(checkinWindowEnd)) {
            throw new BadRequestException(String.format("Check-in window has closed. Check-in was available until %d minutes after class start time", checkinAfterMinutes));
        }

        // Perform check-in
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckInTime(now);
        classBookingRepository.save(booking);

        log.info("User {} successfully checked in to class {} at {}", username, booking.getClassSchedule().getClassName(), now);
    }

    @Override
    public List<BookingResponse> getUserBookings(String username) {
        User user = userService.findByUsername(username);
        List<ClassBooking> bookings = classBookingRepository.findByUserOrderByBookingTimeDesc(user);
        
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private void validateClassBookable(ClassSchedule classSchedule) {
        if (classSchedule.getStatus() != ClassStatus.SCHEDULED) {
            throw new BadRequestException("Class is not available for booking. Status: " + classSchedule.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(classSchedule.getStartTime())) {
            throw new BadRequestException("Cannot book a class that has already started");
        }

        // Prevent booking too close to class start time (e.g., 30 minutes before)
        if (now.isAfter(classSchedule.getStartTime().minusMinutes(30))) {
            throw new BadRequestException("Booking is closed. Classes cannot be booked within 30 minutes of start time");
        }
    }

    private void validateNoTimeConflict(User user, ClassSchedule newClassSchedule) {
        List<ClassBooking> activeBookings = classBookingRepository.findByUserAndStatusIn(
                user, List.of(BookingStatus.BOOKED, BookingStatus.CHECKED_IN));
        
        boolean hasConflict = activeBookings.stream().anyMatch(booking -> {
            ClassSchedule existingClass = booking.getClassSchedule();
            return timeOverlaps(
                    existingClass.getStartTime(), existingClass.getEndTime(),
                    newClassSchedule.getStartTime(), newClassSchedule.getEndTime()
            );
        });

        if (hasConflict) {
            throw new BadRequestException("You have a conflicting class booking during this time slot");
        }
    }

    private boolean timeOverlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private UserPackage findSuitableUserPackage(User user, ClassSchedule classSchedule) {
        List<UserPackage> userPackages = userPackageRepository.findByUserAndPackageEntity_CountryOrderByExpiryDateAsc(
                user, classSchedule.getCountry());
        
        return userPackages.stream()
                .filter(up -> up.getStatus() == UserPackageStatus.ACTIVE)
                .filter(up -> !up.isExpired())
                .filter(up -> up.getRemainingCredits() >= classSchedule.getRequiredCredits())
                .findFirst()
                .orElse(null);
    }

    private int getNextWaitlistPosition(ClassSchedule classSchedule) {
        Optional<Integer> maxPosition = classBookingRepository.findMaxWaitlistPositionByClassSchedule(classSchedule);
        return maxPosition.orElse(0) + 1;
    }

    private void deductCreditsFromPackage(UserPackage userPackage, Integer creditsToDeduct) {
        int newCredits = userPackage.getRemainingCredits() - creditsToDeduct;
        userPackage.setRemainingCredits(newCredits);
        
        // Update status if credits are exhausted
        if (newCredits <= 0) {
            userPackage.setStatus(UserPackageStatus.USED_UP);
        }
        
        userPackageRepository.save(userPackage);
    }

    private void refundCreditsToPackage(UserPackage userPackage, Integer creditsToRefund) {
        int newCredits = userPackage.getRemainingCredits() + creditsToRefund;
        userPackage.setRemainingCredits(newCredits);
        
        // Reactivate package if it was marked as used up
        if (userPackage.getStatus() == UserPackageStatus.USED_UP && !userPackage.isExpired()) {
            userPackage.setStatus(UserPackageStatus.ACTIVE);
        }
        
        userPackageRepository.save(userPackage);
    }

    private void promoteFromWaitlist(ClassSchedule classSchedule) {
        Optional<ClassBooking> nextWaitlisted = classBookingRepository
                .findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
                        classSchedule, BookingStatus.WAITLISTED);

        if (nextWaitlisted.isPresent()) {
            ClassBooking waitlistedBooking = nextWaitlisted.get();
            
            // Check if user's package is still valid and has sufficient credits
            UserPackage userPackage = waitlistedBooking.getUserPackage();
            if (userPackage.isActive() && userPackage.getRemainingCredits() >= classSchedule.getRequiredCredits()) {
                // Promote to booked status
                waitlistedBooking.setStatus(BookingStatus.BOOKED);
                waitlistedBooking.setWaitlistPosition(null);
                
                // Deduct credits
                deductCreditsFromPackage(userPackage, classSchedule.getRequiredCredits());
                
                classBookingRepository.save(waitlistedBooking);
                
                log.info("User {} promoted from waitlist to booked for class {}", 
                    waitlistedBooking.getUser().getUsername(), classSchedule.getClassName());
                
                // Update remaining waitlist positions
                updateWaitlistPositions(classSchedule, waitlistedBooking.getWaitlistPosition());
            } else {
                // User's package is no longer valid, cancel their waitlist booking
                waitlistedBooking.setStatus(BookingStatus.CANCELLED);
                waitlistedBooking.setCancellationTime(LocalDateTime.now());
                classBookingRepository.save(waitlistedBooking);
                
                log.info("Waitlisted user {} booking cancelled due to invalid package for class {}", 
                    waitlistedBooking.getUser().getUsername(), classSchedule.getClassName());
                
                // Try to promote the next person in line
                promoteFromWaitlist(classSchedule);
            }
        }
    }

    private void updateWaitlistPositions(ClassSchedule classSchedule, Integer removedPosition) {
        if (removedPosition == null) return;
        
        List<ClassBooking> waitlistedBookings = classBookingRepository
                .findByClassScheduleAndStatusAndWaitlistPositionGreaterThanOrderByWaitlistPositionAsc(
                        classSchedule, BookingStatus.WAITLISTED, removedPosition);
        
        for (ClassBooking booking : waitlistedBookings) {
            booking.setWaitlistPosition(booking.getWaitlistPosition() - 1);
        }
        
        if (!waitlistedBookings.isEmpty()) {
            classBookingRepository.saveAll(waitlistedBookings);
        }
    }

    @Transactional
    public void processCompletedClasses() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find classes that have ended but are still marked as scheduled
        List<ClassSchedule> completedClasses = classScheduleRepository.findByStatusAndEndTimeBefore(
                ClassStatus.SCHEDULED, now);

        for (ClassSchedule classSchedule : completedClasses) {
            // Update class status to completed
            classSchedule.setStatus(ClassStatus.COMPLETED);
            classScheduleRepository.save(classSchedule);

            // Process waitlisted bookings - refund credits and cancel
            List<ClassBooking> waitlistedBookings = classBookingRepository.findByClassScheduleAndStatus(
                    classSchedule, BookingStatus.WAITLISTED);

            for (ClassBooking booking : waitlistedBookings) {
                // Waitlisted users get their credits back since they never got to attend
                // Note: Credits were never deducted for waitlisted users, so no refund needed
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancellationTime(now);
                classBookingRepository.save(booking);
                
                log.info("Waitlisted booking {} automatically cancelled after class completion", booking.getId());
            }

            // Mark no-shows for booked users who didn't check in
            List<ClassBooking> bookedBookings = classBookingRepository.findByClassScheduleAndStatus(
                    classSchedule, BookingStatus.BOOKED);
            
            for (ClassBooking booking : bookedBookings) {
                booking.setStatus(BookingStatus.NO_SHOW);
                classBookingRepository.save(booking);
                
                log.info("User {} marked as no-show for class {}", 
                    booking.getUser().getUsername(), classSchedule.getClassName());
            }

            log.info("Processed completed class: {} with {} waitlisted and {} no-show bookings", 
                classSchedule.getClassName(), waitlistedBookings.size(), bookedBookings.size());
        }
    }

    private ClassScheduleResponse convertToClassScheduleResponse(ClassSchedule classSchedule) {
        long bookedCount = classBookingRepository.countByClassScheduleAndStatus(
                classSchedule, BookingStatus.BOOKED);
        long waitlistCount = classBookingRepository.countByClassScheduleAndStatus(
                classSchedule, BookingStatus.WAITLISTED);

        return new ClassScheduleResponse(
                classSchedule.getId(),
                classSchedule.getClassName(),
                classSchedule.getDescription(),
                classSchedule.getInstructor(),
                classSchedule.getStartTime(),
                classSchedule.getEndTime(),
                classSchedule.getMaxCapacity(),
                (int) bookedCount,
                (int) waitlistCount,
                classSchedule.getRequiredCredits(),
                classSchedule.getCountry(),
                classSchedule.getLocation(),
                classSchedule.getClassType()
        );
    }

    private BookingResponse convertToBookingResponse(ClassBooking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getClassSchedule().getClassName(),
                booking.getClassSchedule().getInstructor(),
                booking.getClassSchedule().getStartTime(),
                booking.getClassSchedule().getEndTime(),
                booking.getClassSchedule().getLocation(),
                booking.getStatus().toString(),
                booking.getBookingTime(),
                booking.getCancellationTime(),
                booking.getCheckInTime(),
                booking.getWaitlistPosition()
        );
    }
}