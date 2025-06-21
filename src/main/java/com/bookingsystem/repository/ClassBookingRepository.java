package com.bookingsystem.repository;

import com.bookingsystem.entity.ClassBooking;
import com.bookingsystem.entity.ClassSchedule;
import com.bookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByUserOrderByBookingTimeDesc(User user);
    List<ClassBooking> findByUserAndStatus(User user, ClassBooking.BookingStatus status);
    long countByClassScheduleAndStatus(ClassSchedule classSchedule, ClassBooking.BookingStatus status);
    
    Optional<ClassBooking> findFirstByClassScheduleAndStatusOrderByWaitlistPositionAsc(
            ClassSchedule classSchedule, ClassBooking.BookingStatus status);
    
    @Query("SELECT MAX(cb.waitlistPosition) FROM ClassBooking cb WHERE cb.classSchedule = :classSchedule AND cb.status = 'WAITLISTED'")
    Optional<Integer> findMaxWaitlistPositionByClassSchedule(@Param("classSchedule") ClassSchedule classSchedule);
    
    List<ClassBooking> findByClassScheduleAndStatus(ClassSchedule classSchedule, ClassBooking.BookingStatus status);
}