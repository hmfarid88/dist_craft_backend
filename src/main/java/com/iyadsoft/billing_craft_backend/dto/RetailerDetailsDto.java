package com.iyadsoft.billing_craft_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetailerDetailsDto {
    private LocalDate date;
    private String invoice;
    private Double productAmount;
    private Double vatAmount;
    private Double payment;
}
