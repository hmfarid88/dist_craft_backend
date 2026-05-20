package com.iyadsoft.billing_craft_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleEditDto {
    private Long saleId;
    private String saleNote;
    private String category;
    private String brand;
    private String productName;
    private String productno;
    private Double pprice;
    private Double sprice;
    private Double discount;
    private Double offer;
    private LocalDate date;
    private String cid;
}
