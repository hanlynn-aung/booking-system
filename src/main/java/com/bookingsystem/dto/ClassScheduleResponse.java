package com.bookingsystem.dto;

import java.time.LocalDateTime;

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

    // Constructors
    public ClassScheduleResponse() {}

    public ClassScheduleResponse(Long id, String className, String description, String instructor,
                                LocalDateTime startTime, LocalDateTime endTime, Integer maxCapacity,
                                Integer bookedCount, Integer waitlistCount, Integer requiredCredits,
                                String country, String location, String classType) {
        this.id = id;
        this.className = className;
        this.description = description;
        this.instructor = instructor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.bookedCount = bookedCount;
        this.waitlistCount = waitlistCount;
        this.requiredCredits = requiredCredits;
        this.country = country;
        this.location = location;
        this.classType = classType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public Integer getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(Integer waitlistCount) { this.waitlistCount = waitlistCount; }

    public Integer getRequiredCredits() { return requiredCredits; }
    public void setRequiredCredits(Integer requiredCredits) { this.requiredCredits = requiredCredits; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
}