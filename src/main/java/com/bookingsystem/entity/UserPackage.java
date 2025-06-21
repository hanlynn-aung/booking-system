package com.bookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_packages")
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
    private LocalDateTime purchaseDate;

    @NotNull
    private LocalDateTime expiryDate;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserPackageStatus status = UserPackageStatus.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public UserPackage() {}

    public UserPackage(User user, Package packageEntity, Integer remainingCredits, LocalDateTime expiryDate, BigDecimal paidAmount) {
        this.user = user;
        this.packageEntity = packageEntity;
        this.remainingCredits = remainingCredits;
        this.purchaseDate = LocalDateTime.now();
        this.expiryDate = expiryDate;
        this.paidAmount = paidAmount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Package getPackageEntity() { return packageEntity; }
    public void setPackageEntity(Package packageEntity) { this.packageEntity = packageEntity; }

    public Integer getRemainingCredits() { return remainingCredits; }
    public void setRemainingCredits(Integer remainingCredits) { this.remainingCredits = remainingCredits; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public UserPackageStatus getStatus() { return status; }
    public void setStatus(UserPackageStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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