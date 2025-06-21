package com.bookingsystem.controller;

import com.bookingsystem.dto.PackageResponse;
import com.bookingsystem.dto.PurchasePackageRequest;
import com.bookingsystem.dto.UserPackageResponse;
import com.bookingsystem.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Management", description = "Package and subscription management endpoints")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @GetMapping
    @Operation(summary = "Get available packages by country")
    public ResponseEntity<List<PackageResponse>> getAvailablePackages(@RequestParam String country) {
        List<PackageResponse> packages = packageService.getAvailablePackages(country);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/my-packages")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user's purchased packages")
    public ResponseEntity<List<UserPackageResponse>> getUserPackages(Authentication authentication) {
        String username = authentication.getName();
        List<UserPackageResponse> packages = packageService.getUserPackages(username);
        return ResponseEntity.ok(packages);
    }

    @PostMapping("/purchase")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Purchase a package")
    public ResponseEntity<UserPackageResponse> purchasePackage(
            @Valid @RequestBody PurchasePackageRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        UserPackageResponse purchasedPackage = packageService.purchasePackage(username, request);
        return ResponseEntity.ok(purchasedPackage);
    }
}