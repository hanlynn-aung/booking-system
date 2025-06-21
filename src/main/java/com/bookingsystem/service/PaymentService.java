package com.bookingsystem.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    public boolean addPaymentCard(String cardNumber, String expiryMonth, String expiryYear, String cvv, String cardHolderName) {
        // Mock implementation - replace with actual payment gateway
        System.out.println("Adding payment card for: " + cardHolderName);
        return true;
    }

    public boolean paymentCharge(String cardId, BigDecimal amount, String currency, String description) {
        // Mock implementation - replace with actual payment gateway
        System.out.println("Processing payment charge: " + amount + " " + currency);
        System.out.println("Description: " + description);
        return true;
    }
}