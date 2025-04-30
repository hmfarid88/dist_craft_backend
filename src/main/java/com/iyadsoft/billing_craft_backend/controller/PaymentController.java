package com.iyadsoft.billing_craft_backend.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iyadsoft.billing_craft_backend.dto.PayRecevBalance;
import com.iyadsoft.billing_craft_backend.dto.PayRecevDetails;
import com.iyadsoft.billing_craft_backend.dto.RetailerBalanceDto;
import com.iyadsoft.billing_craft_backend.dto.RetailerDetailsDto;
import com.iyadsoft.billing_craft_backend.dto.SupplierDetailsDto;
import com.iyadsoft.billing_craft_backend.dto.SupplierProductDto;
import com.iyadsoft.billing_craft_backend.dto.SupplierSummaryDTO;
import com.iyadsoft.billing_craft_backend.entity.Expense;
import com.iyadsoft.billing_craft_backend.entity.PaymentName;
import com.iyadsoft.billing_craft_backend.entity.PaymentRecord;
import com.iyadsoft.billing_craft_backend.entity.ProfitWithdraw;
import com.iyadsoft.billing_craft_backend.entity.RetailerInfo;
import com.iyadsoft.billing_craft_backend.entity.RetailerPayment;
import com.iyadsoft.billing_craft_backend.entity.SupplierPayment;
import com.iyadsoft.billing_craft_backend.repository.CustomerRepository;
import com.iyadsoft.billing_craft_backend.repository.ExpenseRepository;
import com.iyadsoft.billing_craft_backend.repository.PaymentNameRepository;
import com.iyadsoft.billing_craft_backend.repository.PaymentRecordRepository;
import com.iyadsoft.billing_craft_backend.repository.ProductSaleRepository;
import com.iyadsoft.billing_craft_backend.repository.ProductStockRepository;
import com.iyadsoft.billing_craft_backend.repository.ProfitWithdrawRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerInfoRepository;
import com.iyadsoft.billing_craft_backend.repository.RetailerPaymentRepository;
import com.iyadsoft.billing_craft_backend.repository.SupplierPaymentRepository;
import com.iyadsoft.billing_craft_backend.service.PayRecevService;
import com.iyadsoft.billing_craft_backend.service.RetailerBalanceService;
import com.iyadsoft.billing_craft_backend.service.RetailerPaymentService;
import com.iyadsoft.billing_craft_backend.service.SmsService;
import com.iyadsoft.billing_craft_backend.service.SupplierBalanceService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private SupplierPaymentRepository supplierPaymentRepository;

    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private PaymentNameRepository paymentNameRepository;

    @Autowired
    private ProfitWithdrawRepository profitWithdrawRepository;

    @Autowired
    private RetailerInfoRepository retailerInfoRepository;

    @Autowired
    private SupplierBalanceService supplierBalanceService;

    @Autowired
    private PayRecevService payRecevService;

    @Autowired
    private RetailerPaymentService retailerPaymentService;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RetailerBalanceService retailerBalanceService;

    @Autowired
    private ProductStockRepository productStockRepository;

    @PostMapping("/addPaymentName")
    public ResponseEntity<?> addPaymentName(@RequestBody PaymentName paymentName) {
        if (paymentNameRepository.existsByUsernameAndPaymentPerson(paymentName.getUsername(),
                paymentName.getPaymentPerson())) {
            throw new DuplicateEntityException("Name " + paymentName.getPaymentPerson() + " is already exists!");
        }
        PaymentName savedName = paymentNameRepository.save(paymentName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedName);
    }

    @PostMapping("/expenseRecord")
    public Expense newExpense(@RequestBody Expense expense) {
        return expenseRepository.save(expense);
    }

    @PostMapping("/paymentRecord")
    public PaymentRecord newItem(@RequestBody PaymentRecord paymentRecord) {
        return paymentRecordRepository.save(paymentRecord);
    }

    @PostMapping("/retailerPayment")
    public RetailerPayment newItem(@RequestBody RetailerPayment retailerPayment) {
        // First, save the payment
        RetailerPayment savedPayment = retailerPaymentRepository.save(retailerPayment);

        // 🔢 Calculate total sale
        Double previousSalesTotal = productSaleRepository
                .findTotalSaleByCustomerName(retailerPayment.getRetailerName(), retailerPayment.getUsername())
                .orElse(0.0);

        // 💰 Get total payments
        Double totalPayment = retailerPaymentRepository
                .findTotalPaidByCustomerName(retailerPayment.getRetailerName(), retailerPayment.getUsername())
                .orElse(0.0);

        // 🧾 Get total VAT
        Double totalVat = customerRepository
                .findTotalVatByCustomerName(retailerPayment.getRetailerName(), retailerPayment.getUsername())
                .orElse(0.0);

        // 📊 Calculate balance
        double balance = (previousSalesTotal + totalVat) - totalPayment;

        // 📱 Get phone number
        String phoneNumber = retailerInfoRepository.findByRetailerNameAndUsername(
                retailerPayment.getRetailerName(),
                retailerPayment.getUsername());

        // 📤 Send SMS
        String smsResponse = smsService.sendSms(
                retailerPayment.getUsername(),
                phoneNumber,
                "Dear " + retailerPayment.getRetailerName() +
                        ", your payment is ৳" + retailerPayment.getAmount() +
                        ". And total due is ৳" + balance +
                        ". Thanks from " + retailerPayment.getUsername() + "!");

        System.out.println("SMS API Response: " + smsResponse);

        return savedPayment;
    }

    @PostMapping("/supplierPayment")
    public SupplierPayment newItem(@RequestBody SupplierPayment supplierPayment) {
        return supplierPaymentRepository.save(supplierPayment);
    }

    @PostMapping("/profitWithdraw")
    public ProfitWithdraw newItem(@RequestBody ProfitWithdraw profitWithdraw) {
        return profitWithdrawRepository.save(profitWithdraw);
    }

    @GetMapping("/getPaymentPerson")
    public List<PaymentName> getPaymentNameByUsername(@RequestParam String username) {
        return paymentNameRepository.getPaymentPersonByUsername(username);
    }

    @GetMapping("/getPaymentRecord")
    public List<PayRecevBalance> getPaymentRecordByUsername(@RequestParam String username) {
        return paymentRecordRepository.findPayRecevSummaryBalances(username);
    }

    @GetMapping("/getPaymentRecord-details")
    public List<PayRecevDetails> getPaymentRecordDetailsByUsername(@RequestParam String username,
            @RequestParam String paymentName) {
        return payRecevService.getPaymentReceiveDetails(username, paymentName);
    }

    @GetMapping("/getSupplierBalance")
    public List<SupplierSummaryDTO> getSupplierRecordByUsername(@RequestParam String username) {
        return supplierBalanceService.getSupplierData(username);
    }

    @GetMapping("/getSupplierBalance-details")
    public List<SupplierDetailsDto> getSupplierDetailsByUsername(@RequestParam String username,
            @RequestParam String supplierName) {
        LocalDate date = LocalDate.now();
        return supplierBalanceService.getSupplierDetails(username, supplierName, date);
    }

    @GetMapping("/getDatewiseSupplier-details")
    public List<SupplierDetailsDto> getDatewiseSupplierDetailsByUsername(@RequestParam String username,
            @RequestParam String supplierName, @RequestParam LocalDate date) {
        return supplierBalanceService.getSupplierDetails(username, supplierName, date);
    }

    @GetMapping("/getMonthlyExpense")
    public List<Expense> getMonthlyExpenseByUsername(@RequestParam String username) {
        return expenseRepository.findExpenseForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseExpense")
    public List<Expense> getDatewiseExpenseByUsername(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return expenseRepository.findExpenseForDatewise(username, startDate, endDate);
    }

    @GetMapping("/getSelectedSum")
    public Double getSelectedExpenseByUsername(@RequestParam String username, int year, int month) {
        return expenseRepository.findSelectedMonthSum(username, year, month);
    }

    @GetMapping("/getDatewiseExpenseSum")
    public Double getDatewiseExpenseSumByUsername(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return expenseRepository.findDatewiseMonthSum(username, startDate, endDate);
    }

    @GetMapping("/getRetailerInfo")
    public List<RetailerInfo> getRetailer(@RequestParam String username) {
        return retailerInfoRepository.findAllByUsernameOrderByRetailerNameAsc(username);
    }

    @GetMapping("/getRetailerPayment")
    public List<RetailerPayment> getRetailerPayForCurrentMonth(@RequestParam String username) {
        return retailerPaymentService.getRetailerPayForCurrentMonth(username);
    }

    @GetMapping("/getRetailerBalance")
    public List<RetailerBalanceDto> getRetailerBalance(@RequestParam String username) {
        LocalDate date = LocalDate.now();
        return retailerBalanceService.getRetailerBalance(username, date);
    }

    @GetMapping("/getDatewiseRetailerBalance")
    public List<RetailerBalanceDto> getDatewiseRetailerBalance(@RequestParam String username,
            @RequestParam LocalDate date) {
        return retailerBalanceService.getRetailerBalance(username, date);
    }

    @GetMapping("/getDeatailsRetailerBalance")
    public List<RetailerDetailsDto> getRetailerBalance(@RequestParam String username,
            @RequestParam String retailerName) {
        return retailerBalanceService.getRetailerDetails(username, retailerName);
    }

    @GetMapping("/getDatewiseRetailerPayment")
    public List<RetailerPayment> getDatewiseRetailerPayForCurrentMonth(@RequestParam String username,
            LocalDate startDate, LocalDate endDate) {
        return retailerPaymentService.getDatewiseRetailerPay(username, startDate, endDate);
    }

    @GetMapping("/getRetailerDue")
    public ResponseEntity<Double> getBalance(@RequestParam String username) {
        Double balance = retailerBalanceService.getBalance(username);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/getProductInfo")
    public List<SupplierProductDto> getProductByInvoice(@RequestParam String username, @RequestParam String supplierInvoice) {
        return productStockRepository.findProductInfoByInvoiceAndUser(username, supplierInvoice);
    }

    @GetMapping("/getRetailerPaymentToEdit")
    public RetailerPayment getRetailerPayment(Long id) {
        return retailerPaymentService.getRetailerPaymentById(id);
    }

    @PutMapping("/updateRetailerPayInfo/{id}")
    public ResponseEntity<?> updateProductInfo(@PathVariable Long id, @RequestBody RetailerPayment retailerPayment) {
        try {
            RetailerPayment updatedpPayment = retailerPaymentService.updateRetailerPayInfo(id, retailerPayment);
            return ResponseEntity.ok(updatedpPayment);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteRetailerPayInfo/{id}")
    public ResponseEntity<?> deleteRetailerPayInfo(@PathVariable Long id) {
        try {
            retailerPaymentService.deleteRetailerPaymentById(id);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Retailer payment deleted successfully."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Something went wrong."));
        }
    }

    @GetMapping("/getRetailerInfoByRetailer")
    public Optional<RetailerInfo> getRetailerInfo(@RequestParam String username, @RequestParam String retailerName) {
        return retailerInfoRepository.findByUsernameAndRetailerName(username, retailerName);
    }

    @PutMapping("/updateRetailerInfo/{id}")
    public ResponseEntity<?> updateRetailerInfo(@PathVariable Long id, @RequestBody RetailerInfo retailerInfo) {
        try {
            RetailerInfo updatedRetailer = retailerBalanceService.updateRetailerInfo(id, retailerInfo);
            return ResponseEntity.ok(updatedRetailer);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}
