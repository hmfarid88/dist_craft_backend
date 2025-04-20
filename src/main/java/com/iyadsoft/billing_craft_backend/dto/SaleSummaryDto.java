package com.iyadsoft.billing_craft_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleSummaryDto {
    private String category;
    private String brand;
    private String productName;
    private String color;
    private Long qty;
    private Double sprice;
}
