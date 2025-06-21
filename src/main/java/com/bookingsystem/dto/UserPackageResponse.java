package com.bookingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserPackageResponse {
    private Long id;
    private String packageName;
    private String country;
    private Integer totalCredits;
    private Integer remainingCredits;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private BigDecimal paidAmount;
    private String status;

    // Constructors
    public UserPackageResponse() {}

    public UserPackageResponse(Long id, String packageName, String country, Integer totalCredits,
                              Integer remainingCredits, LocalDateTime purchaseDate, LocalDateTime expiryDate,
                              BigDecimal paidAmount, String status) {
        this.id = id;
        this.packageName = packageName;
        this.country = country;
        this.totalCredits = totalCredits;
        this.remainingCredits = remainingCredits;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }

    public Integer getRemainingCredits() { return remainingCredits; }
    public void setRemainingCredits(Integer remainingCredits) { this.remainingCredits = remainingCredits; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}