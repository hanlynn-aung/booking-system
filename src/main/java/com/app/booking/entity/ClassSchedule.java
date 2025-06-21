package com.app.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "class_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100)
    private String className;

    @Column(length = 500)
    private String description;

    @NotBlank
    @Column(length = 100)
    private String instructor;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    @Positive
    private Integer maxCapacity;

    @NotNull
    @Positive
    private Integer requiredCredits;

    @NotBlank
    @Column(length = 10)
    private String country;

    @Column(length = 100)
    private String location;

    @Column(length = 50)
    private String classType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ClassStatus status = ClassStatus.SCHEDULED;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "classSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassBooking> bookings;

    // Custom constructor
    public ClassSchedule(String className, String description, String instructor, LocalDateTime startTime, 
                        LocalDateTime endTime, Integer maxCapacity, Integer requiredCredits, String country, String location) {
        this.className = className;
        this.description = description;
        this.instructor = instructor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.requiredCredits = requiredCredits;
        this.country = country;
        this.location = location;
        this.status = ClassStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ClassStatus {
        SCHEDULED,
        ONGOING,
        COMPLETED,
        CANCELLED
    }
}