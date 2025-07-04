package com.app.booking.controller;

import com.app.booking.common.annotation.ApiToken;
import com.app.booking.controller.response.ApiResponse;
import com.app.booking.controller.request.ChangePasswordRequest;
import com.app.booking.controller.response.UserProfileResponse;
import com.app.booking.serviceImpl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "User profile management endpoints")
public class UserController {

    @Autowired
    private UserService userservice;

    @GetMapping("/profile")
    @ApiToken
    @Operation(summary = "Get user profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileResponse profile = userservice.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @ApiToken
    @PostMapping("/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        userservice.changePassword(username, request);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
    }
}