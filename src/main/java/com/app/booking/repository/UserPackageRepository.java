package com.app.booking.repository;

import com.app.booking.entity.User;
import com.app.booking.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findByUserOrderByPurchaseDateDesc(User user);
    List<UserPackage> findByUserAndPackageEntity_CountryOrderByExpiryDateAsc(User user, String country);
}