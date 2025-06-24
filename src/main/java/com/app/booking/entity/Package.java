package com.app.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull
    @Positive
    private Integer credits;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Positive
    private Integer validityDays;

    @NotBlank
    @Column(length = 20)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private PackageStatus status = PackageStatus.ACTIVE;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Custom constructor
    public Package(String name, String description, Integer credits, BigDecimal price, Integer validityDays, String country) {
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.price = price;
        this.validityDays = validityDays;
        this.country = country;
        this.status = PackageStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PackageStatus {
        ACTIVE,
        INACTIVE
    }
}