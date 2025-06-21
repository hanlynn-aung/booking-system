package com.app.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}