package com.bookingsystem.dto;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private String className;
    private String instructor;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status;
    private LocalDateTime bookingTime;
    private LocalDateTime cancellationTime;
    private LocalDateTime checkInTime;
    private Integer waitlistPosition;

    // Constructors
    public BookingResponse() {}

    public BookingResponse(Long id, String className, String instructor, LocalDateTime startTime,
                          LocalDateTime endTime, String location, String status, LocalDateTime bookingTime,
                          LocalDateTime cancellationTime, LocalDateTime checkInTime, Integer waitlistPosition) {
        this.id = id;
        this.className = className;
        this.instructor = instructor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.status = status;
        this.bookingTime = bookingTime;
        this.cancellationTime = cancellationTime;
        this.checkInTime = checkInTime;
        this.waitlistPosition = waitlistPosition;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public LocalDateTime getCancellationTime() { return cancellationTime; }
    public void setCancellationTime(LocalDateTime cancellationTime) { this.cancellationTime = cancellationTime; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public Integer getWaitlistPosition() { return waitlistPosition; }
    public void setWaitlistPosition(Integer waitlistPosition) { this.waitlistPosition = waitlistPosition; }
}