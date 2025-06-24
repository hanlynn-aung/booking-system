package com.app.booking.controller;

import com.app.booking.common.annotation.ApiToken;
import com.app.booking.controller.response.PackageResponse;
import com.app.booking.controller.request.PurchasePackageRequest;
import com.app.booking.controller.response.UserPackageResponse;
import com.app.booking.service.PackageService;
import com.app.booking.serviceImpl.PackageServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "Package Management", description = "Package and subscription management endpoints")
public class PackageController {

    private final PackageService packageService;

    @ApiToken
    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get available packages by country")
    public ResponseEntity<List<PackageResponse>> getAvailablePackages(@RequestParam String country) {
        List<PackageResponse> packages = packageService.getAvailablePackages(country);
        return ResponseEntity.ok(packages);
    }

    @ApiToken
    @GetMapping("/my-packages")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get user's purchased packages")
    public ResponseEntity<List<UserPackageResponse>> getUserPackages(Authentication authentication) {
        String username = authentication.getName();
        List<UserPackageResponse> packages = packageService.getUserPackages(username);
        return ResponseEntity.ok(packages);
    }

    @ApiToken
    @PostMapping("/purchase")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Purchase a package")
    public ResponseEntity<UserPackageResponse> purchasePackage(
            @Valid @RequestBody PurchasePackageRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        UserPackageResponse purchasedPackage = packageService.purchasePackage(username, request);
        return ResponseEntity.ok(purchasedPackage);
    }
}