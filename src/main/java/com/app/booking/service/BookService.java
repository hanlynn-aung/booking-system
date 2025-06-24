package com.app.booking.service;

import com.app.booking.controller.response.BookingResponse;
import com.app.booking.controller.response.ClassScheduleResponse;
import com.app.booking.entity.ClassSchedule;

import java.util.List;

public interface BookService {

    List<ClassScheduleResponse> getClassSchedules(String country);

    BookingResponse bookClass(String username, Long classScheduleId);

    void cancelBooking(String username, Long bookingId);

    void checkInToClass(String username, Long bookingId);

    List<BookingResponse> getUserBookings(String username);
}
