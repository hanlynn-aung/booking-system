package com.app.booking.repository;

import com.app.booking.common.constant.ClassStatus;
import com.app.booking.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByCountryAndStatusOrderByStartTime(String country, ClassStatus status);
    List<ClassSchedule> findByStatusAndEndTimeBefore(ClassStatus status, LocalDateTime endTime);
}