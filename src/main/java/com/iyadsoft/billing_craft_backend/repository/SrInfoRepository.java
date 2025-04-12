package com.iyadsoft.billing_craft_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iyadsoft.billing_craft_backend.entity.SrInfo;

public interface SrInfoRepository extends JpaRepository<SrInfo, Integer>{

    Optional<SrInfo> findByUsernameAndSrName(String username, String srName);

    List<SrInfo> findByUsername(String username);
    
}
