package com.app.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}