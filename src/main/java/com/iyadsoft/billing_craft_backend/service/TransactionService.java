package com.iyadsoft.billing_craft_backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iyadsoft.billing_craft_backend.entity.Expense;
import com.iyadsoft.billing_craft_backend.entity.PaymentRecord;
import com.iyadsoft.billing_craft_backend.entity.RetailerPayment;
import com.iyadsoft.billing_craft_backend.entity.SupplierPayment;
import com.iyadsoft.billing_craft_backend.repository.ExpenseRepository;
import com.iyadsoft.billing_craft_backend.repository.PaymentRecordRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;
import com.iyadsoft.billing_craft_backend.repository.SupplierPaymentRepository;

@Service
public class TransactionService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private SupplierPaymentRepository supplierPaymentRepository;

    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    public List<Expense> getLast30DaysExpenses(String username) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(29);
        return expenseRepository.findByUsernameAndDateGreaterThanEqualOrderByDateDesc(username, thirtyDaysAgo);
    }

    public List<PaymentRecord> getLast30DaysOfficePayment(String username) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(29);
        return paymentRecordRepository.findByUsernameAndDateGreaterThanEqualOrderByDateDesc(username, thirtyDaysAgo);
    }

    public List<SupplierPayment> getLast30DaysSupplierPayment(String username) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(29);
        return supplierPaymentRepository.findByUsernameAndDateGreaterThanEqualOrderByDateDesc(username, thirtyDaysAgo);
    }

    public List<RetailerPayment> getLast30DaysRetailerPayment(String username) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(29);
        return retailerPaymentRepository.findByUsernameAndDateGreaterThanEqualOrderByDateDesc(username, thirtyDaysAgo);
    }
}
