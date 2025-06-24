package com.app.booking.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public interface PaymentService {

    boolean addPaymentCard(String cardNumber, String expiryMonth, String expiryYear, String cvv, String cardHolderName);

    boolean paymentCharge(String cardId, BigDecimal price, String usd, String description);
}
