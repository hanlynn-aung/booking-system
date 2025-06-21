package com.app.booking.scheduler;

import com.app.booking.entity.ClassBooking;
import com.app.booking.entity.ClassSchedule;
import com.app.booking.entity.UserPackage;
import com.app.booking.repository.ClassBookingRepository;
import com.app.booking.repository.ClassScheduleRepository;
import com.app.booking.repository.UserPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processCompletedClasses() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find classes that have ended
        List<ClassSchedule> completedClasses = classScheduleRepository.findByStatusAndEndTimeBefore(
                ClassSchedule.ClassStatus.SCHEDULED, now);

        for (ClassSchedule classSchedule : completedClasses) {
            // Update class status
            classSchedule.setStatus(ClassSchedule.ClassStatus.COMPLETED);
            classScheduleRepository.save(classSchedule);

            // Refund credits for waitlisted users
            List<ClassBooking> waitlistedBookings = classBookingRepository.findByClassScheduleAndStatus(
                    classSchedule, ClassBooking.BookingStatus.WAITLISTED);

            for (ClassBooking booking : waitlistedBookings) {
                UserPackage userPackage = booking.getUserPackage();
                userPackage.setRemainingCredits(userPackage.getRemainingCredits() + classSchedule.getRequiredCredits());
                userPackageRepository.save(userPackage);

                booking.setStatus(ClassBooking.BookingStatus.CANCELLED);
                booking.setCancellationTime(now);
                classBookingRepository.save(booking);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void updateExpiredPackages() {
        LocalDateTime now = LocalDateTime.now();
        
        // This would typically be done with a database query, but for simplicity:
        List<UserPackage> allPackages = userPackageRepository.findAll();
        
        for (UserPackage userPackage : allPackages) {
            if (userPackage.isExpired() && userPackage.getStatus() == UserPackage.UserPackageStatus.ACTIVE) {
                userPackage.setStatus(UserPackage.UserPackageStatus.EXPIRED);
                userPackageRepository.save(userPackage);
            }
        }
    }
}