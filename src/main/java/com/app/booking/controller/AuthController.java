package com.app.booking.controller;

import com.app.booking.common.constant.UserStatus;
import com.app.booking.controller.request.LoginRequest;
import com.app.booking.controller.request.RegisterRequest;
import com.app.booking.controller.request.ResetPasswordRequest;
import com.app.booking.controller.response.ApiResponse;
import com.app.booking.controller.response.AuthResponse;
import com.app.booking.entity.User;
import com.app.booking.security.JwtTokenProvider;
import com.app.booking.security.TokenPayload;
import com.app.booking.serviceImpl.UserService;
import com.app.booking.util.Builder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final UserService userservice;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = Builder.of(User::new)
                .add(User::setUsername, request.getUsername())
                .add(User::setPassword, request.getPassword())
                .add(User::setEmail, request.getEmail())
                .add(User::setPhoneNumber, request.getPhoneNumber())
                .add(User::setFirstName, request.getFirstName())
                .add(User::setLastName, request.getLastName())
                        .build();

        userservice.createUser(user);

        ApiResponse apiResponse = Builder.of(ApiResponse::new)
                .add(ApiResponse::setSuccess, true)
                .add(ApiResponse::setMessage, "User registered successfully. Please check your email for verification.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userservice.findByUsername(request.getUsername());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.badRequest().build();
        }

        UserDetails userDetails = userservice.loadUserByUsername(request.getUsername());

        TokenPayload tokenPayload = Builder.of(TokenPayload::new)
                .add(TokenPayload::setUsername, userDetails.getUsername())
                .add(TokenPayload::setAuthorities, userDetails.getAuthorities())
                .build();

        AuthResponse authResponse = jwtTokenProvider.generateToken(tokenPayload);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean verified = userservice.verifyEmail(token);

        ApiResponse apiResponse;
        if (verified) {
            apiResponse = Builder.of(ApiResponse::new)
                    .add(ApiResponse::setSuccess, true)
                    .add(ApiResponse::setMessage, "Email verified successfully")
                    .build();
            return ResponseEntity.ok(apiResponse);
        }

        apiResponse = Builder.of(ApiResponse::new)
                .add(ApiResponse::setSuccess, false)
                .add(ApiResponse::setMessage, "Email verification failed")
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userservice.resetPassword(request.getEmail());

        ApiResponse apiResponse = Builder.of(ApiResponse::new)
                .add(ApiResponse::setSuccess, true)
                .add(ApiResponse::setMessage, "Password reset email sent successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}