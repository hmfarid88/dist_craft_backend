package com.iyadsoft.billing_craft_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.dto.RetailerBalanceDto;
import com.iyadsoft.billing_craft_backend.dto.RetailerDetailsDto;
import com.iyadsoft.billing_craft_backend.repository.ProductSaleRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerInfoRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;

@Service
public class RetailerBalanceService {
    private final RetailerInfoRepository retailerInfoRepository;
    private final ProductSaleRepository productSaleRepository;
    private final RetailerPaymentRepository retailerPaymentRepository;

    public RetailerBalanceService(RetailerInfoRepository retailerInfoRepository,
            ProductSaleRepository productSaleRepository, RetailerPaymentRepository retailerPaymentRepository) {
        this.retailerInfoRepository = retailerInfoRepository;
        this.productSaleRepository = productSaleRepository;
        this.retailerPaymentRepository = retailerPaymentRepository;
    }

    public List<RetailerBalanceDto> getRetailerBalance(String username, LocalDate date) {
        Map<String, RetailerBalanceDto> resultMap = new HashMap<>();

        // Step 1: Load all retailers
        for (Object[] row : retailerInfoRepository.findAllRetailers()) {
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
        //         .filter(dto -> dto.getTotalProductValue() > 0 || dto.getCurrentPaymentTotal() > 0 || dto.getPreviousPaymentTotal() > 0 || dto.getTodayPreviousPayment() > 0)
        //         .collect(Collectors.toList());

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
}
