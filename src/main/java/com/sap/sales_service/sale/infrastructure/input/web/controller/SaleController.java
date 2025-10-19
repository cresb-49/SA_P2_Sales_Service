package com.sap.sales_service.sale.infrastructure.input.web.controller;

import com.sap.sales_service.sale.infrastructure.input.web.mapper.SaleResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sales")
public class SaleController {
    //Use cases will be implemented here

    //Mapper
    private final SaleResponseMapper saleResponseMapper;
}
