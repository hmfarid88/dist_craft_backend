package com.iyadsoft.billing_craft_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailerBalanceDto {
    private String retailerName;
    private String area;
    private Double totalProductValue;
    private Double todayProductValue;
    private Double currentPaymentTotal;
    private Double previousPaymentTotal;
}
