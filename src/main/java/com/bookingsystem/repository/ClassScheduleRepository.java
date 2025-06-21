package com.bookingsystem.repository;

import com.bookingsystem.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByCountryAndStatusOrderByStartTime(String country, ClassSchedule.ClassStatus status);
    List<ClassSchedule> findByStatusAndEndTimeBefore(ClassSchedule.ClassStatus status, LocalDateTime endTime);
}