package com.sap.sales_service.sale.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Sale {
    private UUID id;
    private UUID clientId;
    private BigDecimal totalAmount;
    private SaleStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private List<SaleLine> saleLines;

    public Sale(UUID clientId, List<SaleLine> saleLines) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.saleLines = saleLines;
        this.totalAmount = saleLines.stream()
                .map(SaleLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.status = SaleStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void validate() {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (saleLines == null || saleLines.isEmpty()) {
            throw new IllegalArgumentException("Sale must have at least one sale line");
        }
        for (SaleLine line : saleLines) {
            line.validate();
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }
    }

    public void markAsPaid() {
        if (status != SaleStatusType.PENDING) {
            throw new IllegalStateException("Only pending sales can be marked as paid");
        }
        this.status = SaleStatusType.PAID;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPaidError() {
        if (status != SaleStatusType.PENDING) {
            throw new IllegalStateException("Only pending sales can be marked as paid error");
        }
        this.status = SaleStatusType.PAID_ERROR;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != SaleStatusType.PENDING) {
            throw new IllegalStateException("Only pending sales can be cancelled");
        }
        this.status = SaleStatusType.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void pending() {
        if (status != SaleStatusType.PAID_ERROR) {
            throw new IllegalStateException("Only paid error sales can be marked as pending");
        }
        this.status = SaleStatusType.PENDING;
        this.updatedAt = LocalDateTime.now();
    }
}
