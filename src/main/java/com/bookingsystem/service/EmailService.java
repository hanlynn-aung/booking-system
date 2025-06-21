package com.bookingsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${app.email.verification-url}")
    private String verificationUrl;

    public boolean sendVerificationEmail(String email, String token) {
        // Mock implementation - replace with actual email service
        System.out.println("Sending verification email to: " + email);
        System.out.println("Verification URL: " + verificationUrl + "?token=" + token);
        return true;
    }

    public boolean sendPasswordResetEmail(String email, String newPassword) {
        // Mock implementation - replace with actual email service
        System.out.println("Sending password reset email to: " + email);
        System.out.println("New password: " + newPassword);
        return true;
    }
}