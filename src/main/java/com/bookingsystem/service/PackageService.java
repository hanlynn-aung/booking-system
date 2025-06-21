package com.bookingsystem.service;

import com.bookingsystem.dto.PackageResponse;
import com.bookingsystem.dto.PurchasePackageRequest;
import com.bookingsystem.dto.UserPackageResponse;
import com.bookingsystem.entity.Package;
import com.bookingsystem.entity.User;
import com.bookingsystem.entity.UserPackage;
import com.bookingsystem.exception.BadRequestException;
import com.bookingsystem.exception.ResourceNotFoundException;
import com.bookingsystem.repository.PackageRepository;
import com.bookingsystem.repository.UserPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    public List<PackageResponse> getAvailablePackages(String country) {
        List<Package> packages = packageRepository.findByCountryAndStatus(country, Package.PackageStatus.ACTIVE);
        return packages.stream()
                .map(this::convertToPackageResponse)
                .collect(Collectors.toList());
    }

    public List<UserPackageResponse> getUserPackages(String username) {
        User user = userService.findByUsername(username);
        List<UserPackage> userPackages = userPackageRepository.findByUserOrderByPurchaseDateDesc(user);
        
        return userPackages.stream()
                .map(this::convertToUserPackageResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserPackageResponse purchasePackage(String username, PurchasePackageRequest request) {
        User user = userService.findByUsername(username);
        Package packageEntity = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        if (packageEntity.getStatus() != Package.PackageStatus.ACTIVE) {
            throw new BadRequestException("Package is not available for purchase");
        }

        // Process payment
        boolean paymentSuccess = paymentService.paymentCharge(
                request.getCardId(),
                packageEntity.getPrice(),
                "USD",
                "Package purchase: " + packageEntity.getName()
        );

        if (!paymentSuccess) {
            throw new BadRequestException("Payment processing failed");
        }

        // Create user package
        UserPackage userPackage = new UserPackage(
                user,
                packageEntity,
                packageEntity.getCredits(),
                LocalDateTime.now().plusDays(packageEntity.getValidityDays()),
                packageEntity.getPrice()
        );

        UserPackage savedUserPackage = userPackageRepository.save(userPackage);
        return convertToUserPackageResponse(savedUserPackage);
    }

    private PackageResponse convertToPackageResponse(Package packageEntity) {
        return new PackageResponse(
                packageEntity.getId(),
                packageEntity.getName(),
                packageEntity.getDescription(),
                packageEntity.getCredits(),
                packageEntity.getPrice(),
                packageEntity.getValidityDays(),
                packageEntity.getCountry()
        );
    }

    private UserPackageResponse convertToUserPackageResponse(UserPackage userPackage) {
        return new UserPackageResponse(
                userPackage.getId(),
                userPackage.getPackageEntity().getName(),
                userPackage.getPackageEntity().getCountry(),
                userPackage.getPackageEntity().getCredits(),
                userPackage.getRemainingCredits(),
                userPackage.getPurchaseDate(),
                userPackage.getExpiryDate(),
                userPackage.getPaidAmount(),
                userPackage.isExpired() ? "EXPIRED" : userPackage.getStatus().toString()
        );
    }
}