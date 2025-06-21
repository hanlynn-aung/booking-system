package com.app.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleResponse {
    private Long id;
    private String className;
    private String description;
    private String instructor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCapacity;
    private Integer bookedCount;
    private Integer waitlistCount;
    private Integer requiredCredits;
    private String country;
    private String location;
    private String classType;
}