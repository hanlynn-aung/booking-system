package com.app.booking.repository;

import com.app.booking.common.constant.BookingStatus;
import com.app.booking.entity.ClassBooking;
import com.app.booking.entity.ClassSchedule;
import com.app.booking.entity.User;
import com.app.booking.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    
    // Basic queries
    List<ClassBooking> findByUserOrderByBookingTimeDesc(User user);
    List<ClassBooking> findByUserAndStatus(User user, BookingStatus status);
    List<ClassBooking> findByUserAndStatusIn(User user, List<BookingStatus> statuses);
    
    // Class schedule related queries
    long countByClassScheduleAndStatus(ClassSchedule classSchedule, BookingStatus status);
    List<ClassBooking> findByClassScheduleAndStatus(ClassSchedule classSchedule, BookingStatus status);
    
    // Waitlist management queries
    Optional<ClassBooking> findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
            ClassSchedule classSchedule, BookingStatus status);
    
    @Query("SELECT MAX(cb.waitlistPosition) FROM ClassBooking cb WHERE cb.classSchedule = :classSchedule AND cb.status = 'WAITLISTED'")
    Optional<Integer> findMaxWaitlistPositionByClassSchedule(@Param("classSchedule") ClassSchedule classSchedule);
    
    List<ClassBooking> findByClassScheduleAndStatusAndWaitlistPositionGreaterThanOrderByWaitlistPositionAsc(
            ClassSchedule classSchedule, BookingStatus status, Integer waitlistPosition);
    
    // Time conflict checking
    @Query("SELECT cb FROM ClassBooking cb WHERE cb.user = :user AND cb.status IN ('BOOKED', 'CHECKED_IN') " +
           "AND cb.classSchedule.startTime < :endTime AND cb.classSchedule.endTime > :startTime")
    List<ClassBooking> findConflictingBookings(@Param("user") User user, 
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);
    
    // Statistics and reporting
    @Query("SELECT COUNT(cb) FROM ClassBooking cb WHERE cb.classSchedule = :classSchedule AND cb.status = 'CHECKED_IN'")
    long countCheckedInByClassSchedule(@Param("classSchedule") ClassSchedule classSchedule);
    
    @Query("SELECT COUNT(cb) FROM ClassBooking cb WHERE cb.classSchedule = :classSchedule AND cb.status = 'NO_SHOW'")
    long countNoShowByClassSchedule(@Param("classSchedule") ClassSchedule classSchedule);
    
    // Additional queries for expired package handling
    @Query("SELECT cb FROM ClassBooking cb WHERE cb.userPackage = :userPackage " +
           "AND cb.classSchedule.startTime > :now AND cb.status IN ('BOOKED', 'WAITLISTED')")
    List<ClassBooking> findFutureBookingsByUserPackage(@Param("userPackage") UserPackage userPackage, 
                                                      @Param("now") LocalDateTime now);
    
    // Cleanup queries for old bookings
    @Query("SELECT cb FROM ClassBooking cb WHERE cb.status = :status " +
           "AND cb.classSchedule.endTime < :cutoffDate")
    List<ClassBooking> findOldBookingsByStatusAndDate(@Param("status") BookingStatus status, 
                                                     @Param("cutoffDate") LocalDateTime cutoffDate);
}