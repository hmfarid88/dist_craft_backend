package com.iyadsoft.billing_craft_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailerDetailsDto {
   
    private LocalDate date;
    private String invoice;
    private String note;
    private Double productAmount;
    private Double vatAmount;
    private Double payment;
    private Double balance;

    public RetailerDetailsDto(LocalDate date,
                              String invoice,
                              String note,
                              Double productAmount,
                              Double vatAmount,
                              Double payment) {
        this.date = date;
        this.invoice = invoice;
        this.note = note;
        this.productAmount = productAmount;
        this.vatAmount = vatAmount;
        this.payment = payment;
    }
}
