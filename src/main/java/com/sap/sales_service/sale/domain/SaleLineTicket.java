package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class SaleLineTicket {
    private UUID id;
    @Setter
    private UUID saleId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private TicketStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // View information of ticket if exists
    @Setter
    private TicketView ticketView;

    public SaleLineTicket(
            UUID id, UUID saleId, Integer quantity, BigDecimal unitPrice,
            BigDecimal totalPrice, TicketStatusType status,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.saleId = saleId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public SaleLineTicket(UUID saleId, Integer quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.saleId = saleId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = TicketStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (saleId == null) {
            throw new IllegalArgumentException("Sale ID cannot be null");
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

    public void use() {
        if (!(this.status == TicketStatusType.PENDING || this.status == TicketStatusType.RESERVED)) {
            throw new RuntimeException("Only pending tickets can be used");
        }
        this.status = TicketStatusType.IN_USE;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!(this.status == TicketStatusType.RESERVED || this.status == TicketStatusType.PENDING)) {
            throw new RuntimeException("Only reserved or pending tickets can be cancelled");
        }
        this.status = TicketStatusType.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void purchase() {
        if (!(this.status == TicketStatusType.RESERVED || this.status == TicketStatusType.PENDING)) {
            throw new RuntimeException("Only reserved tickets can be purchased");
        }
        this.status = TicketStatusType.PURCHASED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve() {
        if (this.status != TicketStatusType.PENDING) {
            throw new RuntimeException("Only cancelled tickets can be reserved again");
        }
        this.status = TicketStatusType.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void pend() {
        if (this.status != TicketStatusType.RESERVED) {
            throw new RuntimeException("Only reserved tickets can be set to pending");
        }
        this.status = TicketStatusType.PENDING;
        this.updatedAt = LocalDateTime.now();
    }


}
