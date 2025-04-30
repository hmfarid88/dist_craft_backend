package com.iyadsoft.billing_craft_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iyadsoft.billing_craft_backend.entity.RetailerInfo;

public interface RetailerInfoRepository extends JpaRepository<RetailerInfo, Integer>{

    Optional<RetailerInfo> findByUsernameAndRetailerName(String username, String retailerName);

    List<RetailerInfo> findByUsername(String username);

    List<RetailerInfo> findAllByUsernameOrderByRetailerNameAsc(String username);

     @Query("SELECT r.phoneNumber FROM RetailerInfo r WHERE r.retailerName = :retailerName AND r.username = :username")
     String findByRetailerNameAndUsername(@Param("retailerName") String retailerName, @Param("username") String username);

     @Query("SELECT r.retailerName, r.area FROM RetailerInfo r ORDER BY r.area ASC")
     List<Object[]> findAllRetailers();

     Optional<RetailerInfo> findById(Long id);

     boolean existsByRetailerNameAndIdNot(String retailerName, Long id);

}
