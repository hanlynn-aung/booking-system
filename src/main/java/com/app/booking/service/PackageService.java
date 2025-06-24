package com.app.booking.service;

import com.app.booking.controller.request.PurchasePackageRequest;
import com.app.booking.controller.response.PackageResponse;
import com.app.booking.controller.response.UserPackageResponse;

import java.util.List;

public interface PackageService {

    List<PackageResponse> getAvailablePackages(String country);

    List<UserPackageResponse> getUserPackages(String username);

    UserPackageResponse purchasePackage(String username, PurchasePackageRequest request);
}
