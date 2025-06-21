package com.app.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchasePackageRequest {
    @NotNull
    private Long packageId;

    @NotBlank
    private String cardId;
}