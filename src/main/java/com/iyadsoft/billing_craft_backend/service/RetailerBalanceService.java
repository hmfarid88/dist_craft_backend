package com.iyadsoft.billing_craft_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.dto.RetailerBalanceDto;
import com.iyadsoft.billing_craft_backend.dto.RetailerDetailsDto;
import com.iyadsoft.billing_craft_backend.entity.RetailerInfo;
import com.iyadsoft.billing_craft_backend.repository.CustomerRepository;
import com.iyadsoft.billing_craft_backend.repository.ProductSaleRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerInfoRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class RetailerBalanceService {
    private final RetailerInfoRepository retailerInfoRepository;
    private final ProductSaleRepository productSaleRepository;
    private final RetailerPaymentRepository retailerPaymentRepository;
    private final CustomerRepository customerRepository;

    public RetailerBalanceService(RetailerInfoRepository retailerInfoRepository,
            ProductSaleRepository productSaleRepository, RetailerPaymentRepository retailerPaymentRepository, CustomerRepository customerRepository) {
        this.retailerInfoRepository = retailerInfoRepository;
        this.productSaleRepository = productSaleRepository;
        this.retailerPaymentRepository = retailerPaymentRepository;
        this.customerRepository=customerRepository;
    }

    public List<RetailerBalanceDto> getRetailerBalance(String username, LocalDate date) {
        Map<String, RetailerBalanceDto> resultMap = new HashMap<>();

        // Step 1: Load all retailers
        for (Object[] row : retailerInfoRepository.findAllRetailers(username)) {
            String name = (String) row[0];
            String area = (String) row[1];
            resultMap.put(name, new RetailerBalanceDto(name, area, 0.0, 0.0, 0.0, 0.0, 0.0));
        }

        // Step 2: Add product sales
        for (Object[] row : productSaleRepository.getProductSaleByRetailer(username, date)) {
            String name = (String) row[0];
            Double total = (Double) row[1];
            Double today = (Double) row[2];
            resultMap.computeIfAbsent(name, k -> new RetailerBalanceDto(k, null, 0.0, 0.0, 0.0, 0.0, 0.0));
            RetailerBalanceDto dto = resultMap.get(name);
            dto.setTotalProductValue(total);
            dto.setTodayProductValue(today);
        }

        // Step 3: Add payment data
        for (Object[] row : retailerPaymentRepository.getPaymentsByRetailer(username, date)) {
            String name = (String) row[0];
            Double current = (Double) row[1];
            Double previous = (Double) row[2];
            Double previousToday = (Double) row[3];
            resultMap.computeIfAbsent(name, k -> new RetailerBalanceDto(k, null, 0.0, 0.0, 0.0, 0.0, 0.0));
            RetailerBalanceDto dto = resultMap.get(name);
            dto.setCurrentPaymentTotal(current);
            dto.setPreviousPaymentTotal(previous);
            dto.setTodayPreviousPayment(previousToday);

        }

        // Step 4: Return all values with either payment or product sale
        // return resultMap.values().stream()
        // .filter(dto -> dto.getTotalProductValue() > 0 || dto.getCurrentPaymentTotal()
        // > 0 || dto.getPreviousPaymentTotal() > 0 || dto.getTodayPreviousPayment() >
        // 0)
        // .collect(Collectors.toList());

        return new ArrayList<>(resultMap.values());
    }

    public List<RetailerDetailsDto> getRetailerDetails(String username, String retailerName) {
        // Fetch data from each repository method
        List<RetailerDetailsDto> productSales = productSaleRepository
                .findProductSalesByUsernameAndReatilerName(username, retailerName);
        List<RetailerDetailsDto> payments = retailerPaymentRepository
                .findDetailsPaymentByUsernameAndRetailerName(username, retailerName);

        // Combine the results
        List<RetailerDetailsDto> combinedDetails = new ArrayList<>();
        combinedDetails.addAll(productSales);
        combinedDetails.addAll(payments);

        // Sort the combined list by date (assuming date is a field in
        // SupplierDetailsDto)
        combinedDetails.sort(Comparator.comparing(RetailerDetailsDto::getDate));

        return combinedDetails;
    }

    public Double getBalance(String username) {
        Double totalProductValue = productSaleRepository.getTotalProductValue(username).orElse(0.0);
        Double totalPayment = retailerPaymentRepository.getTotalPayment(username).orElse(0.0);

        return totalProductValue - totalPayment;
    }

    // public RetailerInfo updateRetailerInfo(Long id, RetailerInfo
    // updatedRetailerInfo) {
    // Optional<RetailerInfo> existingRetailerOpt =
    // retailerInfoRepository.findById(id);

    // if (existingRetailerOpt.isPresent()) {
    // RetailerInfo existingRetailer = existingRetailerOpt.get();

    // // Only check for duplicate retailer name if it's changed
    // if
    // (!existingRetailer.getRetailerName().equalsIgnoreCase(updatedRetailerInfo.getRetailerName()))
    // {
    // boolean retailerNameExists =
    // retailerInfoRepository.existsByRetailerNameAndIdNot(
    // updatedRetailerInfo.getRetailerName(), id);

    // if (retailerNameExists) {
    // throw new RuntimeException("Retailer name '" +
    // updatedRetailerInfo.getRetailerName()
    // + "' already exists. Try another.");
    // }

    // existingRetailer.setRetailerName(updatedRetailerInfo.getRetailerName());
    // }

    // // Update other fields regardless
    // existingRetailer.setArea(updatedRetailerInfo.getArea());
    // existingRetailer.setPhoneNumber(updatedRetailerInfo.getPhoneNumber());
    // existingRetailer.setAddress(updatedRetailerInfo.getAddress());

    // return retailerInfoRepository.save(existingRetailer);
    // } else {
    // throw new RuntimeException("Retailer not found with ID: " + id);
    // }
    // }

    @Transactional
    public RetailerInfo updateRetailerInfo(Long id, RetailerInfo updatedRetailerInfo) {

        RetailerInfo existingRetailer = retailerInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retailer not found with ID: " + id));

        String oldName = existingRetailer.getRetailerName();
        String newName = updatedRetailerInfo.getRetailerName();

        // Check duplicate name only if changed
        if (!oldName.equalsIgnoreCase(newName)) {

            boolean exists = retailerInfoRepository
                    .existsByRetailerNameAndIdNot(newName, id);

            if (exists) {
                throw new RuntimeException(
                        "Retailer name '" + newName + "' already exists. Try another.");
            }

            // 🔁 Update dependent tables FIRST
            customerRepository.updateSoldByName(oldName, newName);
            retailerPaymentRepository.updateRetailerName(oldName, newName);

            // Update retailer name
            existingRetailer.setRetailerName(newName);
        }

        // Update remaining fields
        existingRetailer.setArea(updatedRetailerInfo.getArea());
        existingRetailer.setPhoneNumber(updatedRetailerInfo.getPhoneNumber());
        existingRetailer.setAddress(updatedRetailerInfo.getAddress());

        return retailerInfoRepository.save(existingRetailer);
    }

}
