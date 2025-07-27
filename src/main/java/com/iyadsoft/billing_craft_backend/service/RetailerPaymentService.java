package com.iyadsoft.billing_craft_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.entity.RetailerPayment;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RetailerPaymentService {
    @Autowired
    RetailerPaymentRepository retailerPaymentRepository;

    public List<RetailerPayment> getRetailerPayForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return retailerPaymentRepository.findRetailerPayByMonth(year, month, username);
    }

    public List<RetailerPayment> getRetailerPayForCurrentDay(String username) {
        return retailerPaymentRepository.findTodaysRetailerPaymentByUsername(username);
    }

    public List<RetailerPayment> getDatewiseRetailerPay(String username, LocalDate startDate, LocalDate endDate) {
        return retailerPaymentRepository.findDatewiseRetailerPaymentByUsername(username, startDate, endDate);
    }

    public RetailerPayment getRetailerPaymentById(Long id) {
        return retailerPaymentRepository.findById(id).orElse(null);
    }

    public RetailerPayment updateRetailerPayInfo(Long id, RetailerPayment updatedRetailerPayment) {
        Optional<RetailerPayment> existingRetailerOpt = retailerPaymentRepository.findById(id);
    
        if (existingRetailerOpt.isPresent()) {
            RetailerPayment existingRetailer = existingRetailerOpt.get();
        
            existingRetailer.setDate(updatedRetailerPayment.getDate());
            existingRetailer.setRetailerName(updatedRetailerPayment.getRetailerName());
            existingRetailer.setPaymentType(updatedRetailerPayment.getPaymentType());
            existingRetailer.setNote(updatedRetailerPayment.getNote());
            existingRetailer.setAmount(updatedRetailerPayment.getAmount());
              
            return retailerPaymentRepository.save(existingRetailer);
        } else {
            throw new RuntimeException("Retailer not found");
        }
    }

    public void deleteRetailerPaymentById(Long id) {
        if (!retailerPaymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Retailer payment info with ID " + id + " not found.");
        }
        retailerPaymentRepository.deleteById(id);
    }
}
