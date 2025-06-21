package com.bookingsystem.dto;

import java.math.BigDecimal;

public class PackageResponse {
    private Long id;
    private String name;
    private String description;
    private Integer credits;
    private BigDecimal price;
    private Integer validityDays;
    private String country;

    // Constructors
    public PackageResponse() {}

    public PackageResponse(Long id, String name, String description, Integer credits, 
                          BigDecimal price, Integer validityDays, String country) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.price = price;
        this.validityDays = validityDays;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}