package com.app.booking.serviceImpl;

import com.app.booking.common.constant.PackageStatus;
import com.app.booking.controller.response.PackageResponse;
import com.app.booking.controller.request.PurchasePackageRequest;
import com.app.booking.controller.response.UserPackageResponse;
import com.app.booking.entity.Package;
import com.app.booking.entity.User;
import com.app.booking.entity.UserPackage;
import com.app.booking.common.exception.BadRequestException;
import com.app.booking.common.exception.ResourceNotFoundException;
import com.app.booking.repository.PackageRepository;
import com.app.booking.repository.UserPackageRepository;
import com.app.booking.service.PackageService;
import com.app.booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;

    private final UserPackageRepository userPackageRepository;

    private final UserService userService;

    private final PaymentService paymentService;

    @Override
    public List<PackageResponse> getAvailablePackages(String country) {
        List<Package> packages = packageRepository.findByCountryAndStatus(country, PackageStatus.ACTIVE);
        return packages.stream()
                .map(this::convertToPackageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPackageResponse> getUserPackages(String username) {
        User user = userService.findByUsername(username);
        List<UserPackage> userPackages = userPackageRepository.findByUserOrderByPurchaseDateDesc(user);
        
        return userPackages.stream()
                .map(this::convertToUserPackageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserPackageResponse purchasePackage(String username, PurchasePackageRequest request) {
        User user = userService.findByUsername(username);
        Package packageEntity = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        if (packageEntity.getStatus() != PackageStatus.ACTIVE) {
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