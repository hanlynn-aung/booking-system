package com.bookingsystem.controller;

import com.bookingsystem.dto.BookingResponse;
import com.bookingsystem.dto.ClassScheduleResponse;
import com.bookingsystem.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Booking Management", description = "Class scheduling and booking endpoints")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/schedules")
    @Operation(summary = "Get class schedules by country")
    public ResponseEntity<List<ClassScheduleResponse>> getClassSchedules(@RequestParam String country) {
        List<ClassScheduleResponse> schedules = bookingService.getClassSchedules(country);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/bookings/{classScheduleId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Book a class")
    public ResponseEntity<BookingResponse> bookClass(
            @PathVariable Long classScheduleId,
            Authentication authentication) {
        String username = authentication.getName();
        BookingResponse booking = bookingService.bookClass(username, classScheduleId);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/bookings/{bookingId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String username = authentication.getName();
        bookingService.cancelBooking(username, bookingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bookings/{bookingId}/checkin")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Check in to a booked class")
    public ResponseEntity<Void> checkInToClass(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String username = authentication.getName();
        bookingService.checkInToClass(username, bookingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user's bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(Authentication authentication) {
        String username = authentication.getName();
        List<BookingResponse> bookings = bookingService.getUserBookings(username);
        return ResponseEntity.ok(bookings);
    }
}