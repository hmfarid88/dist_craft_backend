package com.iyadsoft.billing_craft_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.controller.DuplicateEntityException;
import com.iyadsoft.billing_craft_backend.entity.RetailerInfo;
import com.iyadsoft.billing_craft_backend.entity.ShopInfo;
import com.iyadsoft.billing_craft_backend.entity.SrInfo;
import com.iyadsoft.billing_craft_backend.repository.RetailerInfoRepository;
import com.iyadsoft.billing_craft_backend.repository.ShopInfoRepository;
import com.iyadsoft.billing_craft_backend.repository.SrInfoRepository;

@Service
public class ShopInfoService {
    private final ShopInfoRepository shopInfoRepository;
    private final RetailerInfoRepository retailerInfoRepository;
    private final SrInfoRepository srInfoRepository;

    @Autowired
    public ShopInfoService(ShopInfoRepository shopInfoRepository, RetailerInfoRepository retailerInfoRepository, SrInfoRepository srInfoRepository) {
        this.shopInfoRepository = shopInfoRepository;
        this.retailerInfoRepository = retailerInfoRepository;
        this.srInfoRepository=srInfoRepository;
    }

    public ShopInfo saveOrUpdateShopInfo(ShopInfo shopInfo) {
        Optional<ShopInfo> existingShopInfo = shopInfoRepository.findByUsername(shopInfo.getUsername());

        if (existingShopInfo.isPresent()) {
            // Update existing shop info
            ShopInfo currentShopInfo = existingShopInfo.get();
            currentShopInfo.setShopName(shopInfo.getShopName());
            currentShopInfo.setPhoneNumber(shopInfo.getPhoneNumber());
            currentShopInfo.setAddress(shopInfo.getAddress());
            currentShopInfo.setEmail(shopInfo.getEmail());
            return shopInfoRepository.save(currentShopInfo);
        } else {
            // Save new shop info
            return shopInfoRepository.save(shopInfo);
        }
    }

    public RetailerInfo saveRetailerInfo(RetailerInfo retailerInfo) {
        Optional<RetailerInfo> existingRetailerInfo = retailerInfoRepository
                .findByUsernameAndRetailerName(retailerInfo.getUsername(), retailerInfo.getRetailerName());

        if (existingRetailerInfo.isPresent()) {
            throw new DuplicateEntityException("Retailer name already exists!");
        } else {
            return retailerInfoRepository.save(retailerInfo);
        }
    }
    public SrInfo saveSrInfo(SrInfo srInfo) {
        Optional<SrInfo> existingSrInfo = srInfoRepository
                .findByUsernameAndSrName(srInfo.getUsername(), srInfo.getSrName());

        if (existingSrInfo.isPresent()) {
            throw new DuplicateEntityException("SR name already exists!");
        } else {
            return srInfoRepository.save(srInfo);
        }
    }

    public Optional<ShopInfo> getShopInfoByUsername(String username) {
        return shopInfoRepository.findByUsername(username);
    }

    public List<RetailerInfo> getRetailerInfoByUsername(String username) {
        return retailerInfoRepository.findByUsername(username);
    }

    public List<SrInfo> getSrInfoByUsername(String username) {
        return srInfoRepository.findByUsername(username);
    }
}
