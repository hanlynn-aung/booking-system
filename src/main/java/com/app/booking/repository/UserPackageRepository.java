package com.app.booking.repository;

import com.app.booking.common.constant.UserPackageStatus;
import com.app.booking.entity.User;
import com.app.booking.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    
    // Basic user package queries
    List<UserPackage> findByUserOrderByPurchaseDateDesc(User user);
    List<UserPackage> findByUserAndPackageEntity_CountryOrderByExpiryDateAsc(User user, String country);
    
    // Active package queries
    @Query("SELECT up FROM UserPackage up WHERE up.user = :user AND up.packageEntity.country = :country " +
           "AND up.status = 'ACTIVE' AND up.expiryDate > :now AND up.remainingCredits > 0 " +
           "ORDER BY up.expiryDate ASC")
    List<UserPackage> findActivePackagesByUserAndCountry(@Param("user") User user, 
                                                        @Param("country") String country, 
                                                        @Param("now") LocalDateTime now);
    
    // Expired package queries
    @Query("SELECT up FROM UserPackage up WHERE up.status = 'ACTIVE' AND up.expiryDate < :now")
    List<UserPackage> findExpiredActivePackages(@Param("now") LocalDateTime now);
    
    // Package statistics
    @Query("SELECT COUNT(up) FROM UserPackage up WHERE up.user = :user AND up.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") UserPackageStatus status);
    
    @Query("SELECT SUM(up.remainingCredits) FROM UserPackage up WHERE up.user = :user " +
           "AND up.packageEntity.country = :country AND up.status = 'ACTIVE' AND up.expiryDate > :now")
    Integer getTotalRemainingCreditsByUserAndCountry(@Param("user") User user, 
                                                   @Param("country") String country, 
                                                   @Param("now") LocalDateTime now);
    
    // Package expiry management
    @Query("SELECT up FROM UserPackage up WHERE up.expiryDate BETWEEN :startDate AND :endDate " +
           "AND up.status = 'ACTIVE' ORDER BY up.expiryDate ASC")
    List<UserPackage> findPackagesExpiringBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
}