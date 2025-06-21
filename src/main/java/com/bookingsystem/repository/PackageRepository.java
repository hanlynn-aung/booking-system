package com.bookingsystem.repository;

import com.bookingsystem.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByCountryAndStatus(String country, Package.PackageStatus status);
}