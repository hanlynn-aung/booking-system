package com.app.booking.repository;

import com.app.booking.common.constant.PackageStatus;
import com.app.booking.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByCountryAndStatus(String country, PackageStatus status);
}