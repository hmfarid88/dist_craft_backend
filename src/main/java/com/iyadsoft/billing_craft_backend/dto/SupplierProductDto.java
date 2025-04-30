package com.iyadsoft.billing_craft_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierProductDto {
    private String productName;
    private String color;
    private Double pprice;
    private Long qty;
}
