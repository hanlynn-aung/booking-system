package com.app.booking.scheduler;

import com.app.booking.common.constant.BookingStatus;
import com.app.booking.common.constant.ClassStatus;
import com.app.booking.common.constant.UserPackageStatus;
import com.app.booking.entity.ClassBooking;
import com.app.booking.entity.ClassSchedule;
import com.app.booking.entity.UserPackage;
import com.app.booking.repository.ClassBookingRepository;
import com.app.booking.repository.ClassScheduleRepository;
import com.app.booking.repository.UserPackageRepository;
import com.app.booking.serviceImpl.BookingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class BookingScheduler {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    /**
     * Process completed classes every 5 minutes
     * - Mark classes as completed
     * - Cancel waitlisted bookings (credits were never deducted)
     * - Mark no-shows for users who didn't check in
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processCompletedClasses() {
        try {
            log.debug("Starting scheduled task: processCompletedClasses");
            bookingService.processCompletedClasses();
            log.debug("Completed scheduled task: processCompletedClasses");
        } catch (Exception e) {
            log.error("Error in processCompletedClasses scheduler", e);
        }
    }

    /**
     * Update expired packages daily at 2 AM
     * - Mark packages as expired if past expiry date
     * - Cancel any active bookings for expired packages
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void updateExpiredPackages() {
        try {
            log.info("Starting scheduled task: updateExpiredPackages");
            
            LocalDateTime now = LocalDateTime.now();
            int updatedCount = 0;
            
            // Find all active packages that have expired
            List<UserPackage> expiredPackages = userPackageRepository.findExpiredActivePackages(now);
            
            for (UserPackage userPackage : expiredPackages) {
                // Mark package as expired
                userPackage.setStatus(UserPackageStatus.EXPIRED);
                userPackageRepository.save(userPackage);
                updatedCount++;
                
                // Cancel any future bookings using this expired package
                cancelFutureBookingsForExpiredPackage(userPackage, now);
                
                log.debug("Marked package {} as expired for user {}", 
                    userPackage.getId(), userPackage.getUser().getUsername());
            }
            
            log.info("Completed scheduled task: updateExpiredPackages. Updated {} packages", updatedCount);
            
        } catch (Exception e) {
            log.error("Error in updateExpiredPackages scheduler", e);
        }
    }

    /**
     * Clean up old cancelled and completed bookings monthly
     * This helps maintain database performance by archiving old data
     */
    @Scheduled(cron = "0 0 3 1 * ?") // Run monthly on the 1st at 3 AM
    @Transactional
    public void cleanupOldBookings() {
        try {
            log.info("Starting scheduled task: cleanupOldBookings");
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);
            
            // Find old cancelled bookings
            List<ClassBooking> oldCancelledBookings = classBookingRepository
                .findOldBookingsByStatusAndDate(BookingStatus.CANCELLED, cutoffDate);
            
            // Find old completed class bookings
            List<ClassBooking> oldCompletedBookings = classBookingRepository
                .findOldBookingsByStatusAndDate(BookingStatus.NO_SHOW, cutoffDate);
            
            int cleanedCount = oldCancelledBookings.size() + oldCompletedBookings.size();
            
            // Archive or delete old bookings (implement based on business requirements)
            // For now, we'll just log the count
            log.info("Found {} old bookings that could be archived", cleanedCount);
            
            log.info("Completed scheduled task: cleanupOldBookings");
            
        } catch (Exception e) {
            log.error("Error in cleanupOldBookings scheduler", e);
        }
    }

    /**
     * Send reminder notifications for upcoming classes
     * Run every hour to check for classes starting in the next 2 hours
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional(readOnly = true)
    public void sendClassReminders() {
        try {
            log.debug("Starting scheduled task: sendClassReminders");
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderWindow = now.plusHours(2);
            
            // Find classes starting in the next 2 hours
            List<ClassSchedule> upcomingClasses = classScheduleRepository
                .findByStatusAndStartTimeBetween(ClassStatus.SCHEDULED, now, reminderWindow);
            
            for (ClassSchedule classSchedule : upcomingClasses) {
                // Find all booked users for this class
                List<ClassBooking> bookedUsers = classBookingRepository
                    .findByClassScheduleAndStatus(classSchedule, BookingStatus.BOOKED);
                
                for (ClassBooking booking : bookedUsers) {
                    // Send reminder notification (implement based on requirements)
                    sendClassReminderNotification(booking);
                }
                
                log.debug("Sent reminders for class: {} to {} users", 
                    classSchedule.getClassName(), bookedUsers.size());
            }
            
            log.debug("Completed scheduled task: sendClassReminders");
            
        } catch (Exception e) {
            log.error("Error in sendClassReminders scheduler", e);
        }
    }

    /**
     * Monitor and alert for system health issues
     * Run every 15 minutes to check for potential issues
     */
    @Scheduled(fixedRate = 900000) // Run every 15 minutes
    @Transactional(readOnly = true)
    public void systemHealthCheck() {
        try {
            log.debug("Starting scheduled task: systemHealthCheck");
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourAgo = now.minusHours(1);
            
            // Check for classes with unusually high waitlist counts
            List<ClassSchedule> classesWithHighWaitlist = classScheduleRepository
                .findClassesWithHighWaitlist(10); // Alert if waitlist > 10
            
            if (!classesWithHighWaitlist.isEmpty()) {
                log.warn("Found {} classes with high waitlist counts", classesWithHighWaitlist.size());
                // Send alert to administrators
            }
            
            // Check for failed bookings (implement based on error tracking)
            // Check Redis connection health
            // Check database performance metrics
            
            log.debug("Completed scheduled task: systemHealthCheck");
            
        } catch (Exception e) {
            log.error("Error in systemHealthCheck scheduler", e);
        }
    }

    private void cancelFutureBookingsForExpiredPackage(UserPackage expiredPackage, LocalDateTime now) {
        // Find future bookings using this package
        List<ClassBooking> futureBookings = classBookingRepository
            .findFutureBookingsByUserPackage(expiredPackage, now);
        
        for (ClassBooking booking : futureBookings) {
            if (booking.getStatus() == BookingStatus.BOOKED || booking.getStatus() == BookingStatus.WAITLISTED) {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancellationTime(now);
                classBookingRepository.save(booking);
                
                // If it was a confirmed booking, try to promote from waitlist
                if (booking.getStatus() == BookingStatus.BOOKED) {
                    // This would trigger waitlist promotion logic
                    log.info("Cancelled booking {} due to expired package, checking waitlist promotion", 
                        booking.getId());
                }
            }
        }
    }

    private void sendClassReminderNotification(ClassBooking booking) {
        // Implement notification logic here
        // This could be email, SMS, push notification, etc.
        log.debug("Sending reminder to user {} for class {} starting at {}", 
            booking.getUser().getUsername(),
            booking.getClassSchedule().getClassName(),
            booking.getClassSchedule().getStartTime());
    }
}