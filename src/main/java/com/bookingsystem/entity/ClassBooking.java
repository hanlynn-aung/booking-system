package com.bookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_package_id", nullable = false)
    private UserPackage userPackage;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.BOOKED;

    @NotNull
    @Builder.Default
    private LocalDateTime bookingTime = LocalDateTime.now();

    private LocalDateTime cancellationTime;

    private LocalDateTime checkInTime;

    private Integer waitlistPosition;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Custom constructor
    public ClassBooking(User user, ClassSchedule classSchedule, UserPackage userPackage) {
        this.user = user;
        this.classSchedule = classSchedule;
        this.userPackage = userPackage;
        this.status = BookingStatus.BOOKED;
        this.bookingTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BookingStatus {
        BOOKED,
        WAITLISTED,
        CANCELLED,
        CHECKED_IN,
        NO_SHOW
    }
}