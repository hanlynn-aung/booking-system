package com.app.booking.service;

public interface EmailService {

    boolean sendVerificationEmail(String email, String token);

    boolean sendPasswordResetEmail(String email, String newPassword);
}
