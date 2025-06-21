package com.bookingsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private Long id;
    private String name;
    private String description;
    private Integer credits;
    private BigDecimal price;
    private Integer validityDays;
    private String country;
}