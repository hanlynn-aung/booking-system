package com.app.booking.repository;

import com.app.booking.common.constant.BookingStatus;
import com.app.booking.entity.ClassBooking;
import com.app.booking.entity.ClassSchedule;
import com.app.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByUserOrderByBookingTimeDesc(User user);
    List<ClassBooking> findByUserAndStatus(User user, BookingStatus status);
    long countByClassScheduleAndStatus(ClassSchedule classSchedule, BookingStatus status);
    
    Optional<ClassBooking> findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
            ClassSchedule classSchedule, BookingStatus status);
    
    @Query("SELECT MAX(cb.waitlistPosition) FROM ClassBooking cb WHERE cb.classSchedule = :classSchedule AND cb.status = 'WAITLISTED'")
    Optional<Integer> findMaxWaitlistPositionByClassSchedule(@Param("classSchedule") ClassSchedule classSchedule);
    
    List<ClassBooking> findByClassScheduleAndStatus(ClassSchedule classSchedule, BookingStatus status);
}