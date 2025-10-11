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

    public SaleLine(UUID saleId, UUID snackId, UUID ticketId, Integer quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.saleId = saleId;
        this.snackId = snackId;
        this.ticketId = ticketId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void validate() {
        if(saleId == null) {
            throw new IllegalArgumentException("Sale ID cannot be null");
        }
        if(snackId == null && ticketId == null) {
            throw new IllegalArgumentException("Either Snack ID or Ticket ID must be provided");
        }
        if(snackId != null && ticketId != null) {
            throw new IllegalArgumentException("Only one of Snack ID or Ticket ID can be provided");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total price must be non-negative");
        }
    }
}
