package com.app.booking.repository;

import com.app.booking.common.constant.ClassStatus;
import com.app.booking.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    
    // Basic schedule queries
    List<ClassSchedule> findByCountryAndStatusOrderByStartTime(String country, ClassStatus status);
    List<ClassSchedule> findByStatusAndEndTimeBefore(ClassStatus status, LocalDateTime endTime);
    List<ClassSchedule> findByStatusAndStartTimeBetween(ClassStatus status, LocalDateTime startTime, LocalDateTime endTime);
    
    // Capacity and waitlist management
    @Query("SELECT cs FROM ClassSchedule cs WHERE cs.status = 'SCHEDULED' " +
           "AND (SELECT COUNT(cb) FROM ClassBooking cb WHERE cb.classSchedule = cs AND cb.status = 'WAITLISTED') > :waitlistThreshold")
    List<ClassSchedule> findClassesWithHighWaitlist(@Param("waitlistThreshold") int waitlistThreshold);
    
    // Time-based queries
    @Query("SELECT cs FROM ClassSchedule cs WHERE cs.country = :country AND cs.status = 'SCHEDULED' " +
           "AND cs.startTime > :now ORDER BY cs.startTime ASC")
    List<ClassSchedule> findUpcomingClassesByCountry(@Param("country") String country, @Param("now") LocalDateTime now);
    
    @Query("SELECT cs FROM ClassSchedule cs WHERE cs.status = 'SCHEDULED' " +
           "AND cs.startTime BETWEEN :startTime AND :endTime")
    List<ClassSchedule> findScheduledClassesBetween(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
    
    // Instructor and class type queries
    List<ClassSchedule> findByInstructorAndStatusOrderByStartTime(String instructor, ClassStatus status);
    List<ClassSchedule> findByClassTypeAndCountryAndStatusOrderByStartTime(String classType, String country, ClassStatus status);
    
    // Statistics queries
    @Query("SELECT COUNT(cs) FROM ClassSchedule cs WHERE cs.country = :country AND cs.status = :status " +
           "AND cs.startTime BETWEEN :startDate AND :endDate")
    long countByCountryAndStatusAndDateRange(@Param("country") String country, 
                                           @Param("status") ClassStatus status,
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
}