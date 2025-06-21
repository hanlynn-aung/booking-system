package com.app.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packageEntity;

    @NotNull
    @PositiveOrZero
    private Integer remainingCredits;

    @NotNull
    @Builder.Default
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @NotNull
    private LocalDateTime expiryDate;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private UserPackageStatus status = UserPackageStatus.ACTIVE;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Custom constructor
    public UserPackage(User user, Package packageEntity, Integer remainingCredits, LocalDateTime expiryDate, BigDecimal paidAmount) {
        this.user = user;
        this.packageEntity = packageEntity;
        this.remainingCredits = remainingCredits;
        this.purchaseDate = LocalDateTime.now();
        this.expiryDate = expiryDate;
        this.paidAmount = paidAmount;
        this.status = UserPackageStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isActive() {
        return status == UserPackageStatus.ACTIVE && !isExpired() && remainingCredits > 0;
    }

    public enum UserPackageStatus {
        ACTIVE,
        EXPIRED,
        USED_UP
    }
}