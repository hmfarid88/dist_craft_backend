package com.iyadsoft.billing_craft_backend.entity;

import java.time.LocalDate;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_record", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_payment_name", columnList = "paymentName"),
        @Index(name = "idx_date", columnList = "date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String paymentName;
    private String paymentType;
    private String paymentNote;
    private Double amount;
    private String username;
}
