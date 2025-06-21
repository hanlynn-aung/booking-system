package com.bookingsystem.service;

import com.bookingsystem.dto.BookingResponse;
import com.bookingsystem.dto.ClassScheduleResponse;
import com.bookingsystem.entity.*;
import com.bookingsystem.exception.BadRequestException;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.repository.*;
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
public class BookingService {

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

    @Value("${app.booking.cancellation-hours}")
    private int cancellationHours;

    public List<ClassScheduleResponse> getClassSchedules(String country) {
        List<ClassSchedule> schedules = classScheduleRepository.findByCountryAndStatusOrderByStartTime(
                country, ClassSchedule.ClassStatus.SCHEDULED);
        
        return schedules.stream()
                .map(this::convertToClassScheduleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse bookClass(String username, Long classScheduleId) {
        User user = userService.findByUsername(username);
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Class schedule not found"));

        // Check if class is bookable
        if (classSchedule.getStatus() != ClassSchedule.ClassStatus.SCHEDULED) {
            throw new BadRequestException("Class is not available for booking");
        }

        // Check for time conflicts
        if (hasTimeConflict(user, classSchedule)) {
            throw new BadRequestException("You have a conflicting class booking at this time");
        }

        // Find suitable user package
        UserPackage userPackage = findSuitableUserPackage(user, classSchedule);
        if (userPackage == null) {
            throw new BadRequestException("No suitable package found for this class");
        }

        // Use Redis for concurrent booking control
        String lockKey = "class_booking_lock:" + classScheduleId;
        String lockValue = "lock_" + System.currentTimeMillis();
        
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
        
        if (!lockAcquired) {
            throw new BadRequestException("Class booking is temporarily unavailable. Please try again.");
        }

        try {
            long bookedCount = classBookingRepository.countByClassScheduleAndStatus(
                    classSchedule, ClassBooking.BookingStatus.BOOKED);

            ClassBooking booking = new ClassBooking(user, classSchedule, userPackage);

            if (bookedCount >= classSchedule.getMaxCapacity()) {
                // Add to waitlist
                int waitlistPosition = getNextWaitlistPosition(classSchedule);
                booking.setStatus(ClassBooking.BookingStatus.WAITLISTED);
                booking.setWaitlistPosition(waitlistPosition);
            } else {
                // Deduct credits
                userPackage.setRemainingCredits(userPackage.getRemainingCredits() - classSchedule.getRequiredCredits());
                userPackageRepository.save(userPackage);
            }

            ClassBooking savedBooking = classBookingRepository.save(booking);
            return convertToBookingResponse(savedBooking);

        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public void cancelBooking(String username, Long bookingId) {
        User user = userService.findByUsername(username);
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == ClassBooking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = booking.getClassSchedule().getStartTime();
        boolean isEarlyCancel = now.isBefore(classStartTime.minusHours(cancellationHours));

        booking.setStatus(ClassBooking.BookingStatus.CANCELLED);
        booking.setCancellationTime(now);

        if (booking.getStatus() == ClassBooking.BookingStatus.BOOKED && isEarlyCancel) {
            // Refund credits
            UserPackage userPackage = booking.getUserPackage();
            userPackage.setRemainingCredits(userPackage.getRemainingCredits() + booking.getClassSchedule().getRequiredCredits());
            userPackageRepository.save(userPackage);

            // Move waitlisted user to booked
            promoteFromWaitlist(booking.getClassSchedule());
        }

        classBookingRepository.save(booking);
    }

    @Transactional
    public void checkInToClass(String username, Long bookingId) {
        User user = userService.findByUsername(username);
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only check in to your own bookings");
        }

        if (booking.getStatus() != ClassBooking.BookingStatus.BOOKED) {
            throw new BadRequestException("Only confirmed bookings can be checked in");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStartTime = booking.getClassSchedule().getStartTime();
        
        if (now.isBefore(classStartTime.minusMinutes(15)) || now.isAfter(classStartTime.plusMinutes(30))) {
            throw new BadRequestException("Check-in is only available 15 minutes before to 30 minutes after class start time");
        }

        booking.setStatus(ClassBooking.BookingStatus.CHECKED_IN);
        booking.setCheckInTime(now);
        classBookingRepository.save(booking);
    }

    public List<BookingResponse> getUserBookings(String username) {
        User user = userService.findByUsername(username);
        List<ClassBooking> bookings = classBookingRepository.findByUserOrderByBookingTimeDesc(user);
        
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    private boolean hasTimeConflict(User user, ClassSchedule newClassSchedule) {
        List<ClassBooking> userBookings = classBookingRepository.findByUserAndStatus(user, ClassBooking.BookingStatus.BOOKED);
        
        return userBookings.stream().anyMatch(booking -> {
            ClassSchedule existingClass = booking.getClassSchedule();
            return timeOverlaps(existingClass.getStartTime(), existingClass.getEndTime(),
                    newClassSchedule.getStartTime(), newClassSchedule.getEndTime());
        });
    }

    private boolean timeOverlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private UserPackage findSuitableUserPackage(User user, ClassSchedule classSchedule) {
        List<UserPackage> userPackages = userPackageRepository.findByUserAndPackageEntity_CountryOrderByExpiryDateAsc(
                user, classSchedule.getCountry());
        
        return userPackages.stream()
                .filter(up -> up.isActive() && up.getRemainingCredits() >= classSchedule.getRequiredCredits())
                .findFirst()
                .orElse(null);
    }

    private int getNextWaitlistPosition(ClassSchedule classSchedule) {
        Optional<Integer> maxPosition = classBookingRepository.findMaxWaitlistPositionByClassSchedule(classSchedule);
        return maxPosition.orElse(0) + 1;
    }

    private void promoteFromWaitlist(ClassSchedule classSchedule) {
        Optional<ClassBooking> nextWaitlisted = classBookingRepository
                .findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
                        classSchedule, ClassBooking.BookingStatus.WAITLISTED);

        if (nextWaitlisted.isPresent()) {
            ClassBooking booking = nextWaitlisted.get();
            booking.setStatus(ClassBooking.BookingStatus.BOOKED);
            booking.setWaitlistPosition(null);

            // Deduct credits
            UserPackage userPackage = booking.getUserPackage();
            userPackage.setRemainingCredits(userPackage.getRemainingCredits() - classSchedule.getRequiredCredits());
            userPackageRepository.save(userPackage);

            classBookingRepository.save(booking);
        }
    }

    private ClassScheduleResponse convertToClassScheduleResponse(ClassSchedule classSchedule) {
        long bookedCount = classBookingRepository.countByClassScheduleAndStatus(
                classSchedule, ClassBooking.BookingStatus.BOOKED);
        long waitlistCount = classBookingRepository.countByClassScheduleAndStatus(
                classSchedule, ClassBooking.BookingStatus.WAITLISTED);

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