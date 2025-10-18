package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
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
    private List<SaleLineSnack> saleLineSnacks;
    private List<SaleLineTicket> saleLineTickets;

    public Sale(UUID clientId, List<SaleLineSnack> saleLineSnacks, List<SaleLineTicket> saleLineTickets) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        var amountFromTickets = saleLineTickets.stream()
                .map(SaleLineTicket::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var amountFromSnacks = saleLineSnacks.stream()
                .map(SaleLineSnack::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = amountFromTickets.add(amountFromSnacks);
        this.status = SaleStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void validate() {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        for (SaleLineSnack line : saleLineSnacks) {
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
