package com.iyadsoft.billing_craft_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iyadsoft.billing_craft_backend.dto.PaymentDto;
import com.iyadsoft.billing_craft_backend.dto.ReceiveDto;
import com.iyadsoft.billing_craft_backend.repository.PaymentRecordRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;
import com.iyadsoft.billing_craft_backend.service.CashBookService;

@RestController
@RequestMapping("/cashbook")
public class CashbookController {
      private final RetailerPaymentRepository retailerPaymentRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    public CashbookController(RetailerPaymentRepository retailerPaymentRepository,
                                     PaymentRecordRepository paymentRecordRepository) {
        this.retailerPaymentRepository = retailerPaymentRepository;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    @Autowired
    private CashBookService cashBookService;

    @GetMapping("/net-sum-before-today")
    public Double getNetSumAmountBeforeToday(@RequestParam String username, LocalDate date) {
        return paymentRecordRepository.findNetSumAmountBeforeToday(username, date);
    }

    @GetMapping("/allPaymentSum")
    public Double getAllPayment(@RequestParam String username) {
        return paymentRecordRepository.findAllPaymentSum(username);
    }

    @GetMapping("/allReceiveSum")
    public Double getAllReceive(@RequestParam String username) {
        Double retailerSum = retailerPaymentRepository.findRetailerPaySum(username);
        Double otherSum = paymentRecordRepository.findAllReceiveSum(username);

        // Handle nulls to avoid NullPointerException
        double total = (retailerSum != null ? retailerSum : 0.0) + 
                       (otherSum != null ? otherSum : 0.0);

        return total;
    }

    @GetMapping("/payments/today")
    public List<PaymentDto> getPaymentsForToday(@RequestParam String username, LocalDate date) {
        return cashBookService.getPaymentsForToday(username, date);
    }

    @GetMapping("/receives/today")
    public List<ReceiveDto> getReceivesForToday(@RequestParam String username, LocalDate date) {
        return cashBookService.getReceivesForToday(username, date);
    }

    // @GetMapping("/sales/customer")
    // public List<CashbookSaleDto> getCustomerSalesDetails(@RequestParam String username, @RequestParam LocalDate date) {
    //     return cashBookService.getCustomerSalesDetails(username, date);
    // }
}
