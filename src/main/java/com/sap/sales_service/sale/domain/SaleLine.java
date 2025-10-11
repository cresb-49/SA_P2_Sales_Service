package com.sap.sales_service.sale.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class SaleLine {
    private UUID id;
    private UUID saleId;
    private UUID snackId;
    private UUID ticketId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
