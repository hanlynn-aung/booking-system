package com.app.booking.controller;

import com.app.booking.dto.*;
import com.app.booking.entity.User;
import com.app.booking.service.UserService;
import com.app.booking.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
        );

        userService.createUser(user);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully. Please check your email for verification."));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            return ResponseEntity.badRequest().build();
        }

        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt, "Bearer", user.getUsername(), user.getEmail()));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "Email verification failed"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "Password reset email sent successfully"));
    }
}