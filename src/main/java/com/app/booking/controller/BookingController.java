package com.app.booking.controller;

import com.app.booking.common.annotation.ApiToken;
import com.app.booking.controller.response.BookingResponse;
import com.app.booking.controller.response.ClassScheduleResponse;
import com.app.booking.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "Class scheduling and booking endpoints")
public class BookingController {

    private final BookService bookingService;

    @GetMapping("/schedules")
    @SecurityRequirement(name = "bearer-key")
    @ApiToken
    @Operation(summary = "Get class schedules by country")
    public ResponseEntity<List<ClassScheduleResponse>> getClassSchedules(@RequestParam String country) {
        List<ClassScheduleResponse> schedules = bookingService.getClassSchedules(country);
        return ResponseEntity.ok(schedules);
    }

    @ApiToken
    @PostMapping("/bookings/{classScheduleId}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Book a class")
    public ResponseEntity<BookingResponse> bookClass(
            @PathVariable Long classScheduleId,
            Authentication authentication) {
        String username = authentication.getName();
        BookingResponse booking = bookingService.bookClass(username, classScheduleId);
        return ResponseEntity.ok(booking);
    }


    @ApiToken
    @DeleteMapping("/bookings/{bookingId}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String username = authentication.getName();
        bookingService.cancelBooking(username, bookingId);
        return ResponseEntity.ok().build();
    }

    @ApiToken
    @PostMapping("/bookings/{bookingId}/checkin")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Check in to a booked class")
    public ResponseEntity<Void> checkInToClass(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String username = authentication.getName();
        bookingService.checkInToClass(username, bookingId);
        return ResponseEntity.ok().build();
    }

    @ApiToken
    @GetMapping("/bookings")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get user's bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(Authentication authentication) {
        String username = authentication.getName();
        List<BookingResponse> bookings = bookingService.getUserBookings(username);
        return ResponseEntity.ok(bookings);
    }
}