package com.bookingsystem.repository;

import com.bookingsystem.entity.User;
import com.bookingsystem.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findByUserOrderByPurchaseDateDesc(User user);
    List<UserPackage> findByUserAndPackageEntity_CountryOrderByExpiryDateAsc(User user, String country);
}