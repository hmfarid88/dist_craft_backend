package com.iyadsoft.billing_craft_backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.entity.RetailerPayment;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;

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

    public List<RetailerPayment> getDatewiseRetailerPay(String username, LocalDate startDate, LocalDate endDate) {
        return retailerPaymentRepository.findDatewiseRetailerPaymentByUsername(username, startDate, endDate);
    }
}
