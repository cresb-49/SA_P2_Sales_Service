
package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Sale {
    private UUID id;
    private UUID clientId;
    private UUID cinemaId;
    private BigDecimal totalAmount;
    private BigDecimal claimedAmount;
    private BigDecimal discountedAmount;
    private SaleStatusType status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;

    @Setter
    private List<SaleLineSnack> saleLineSnacks;
    @Setter
    private List<SaleLineTicket> saleLineTickets;

    public Sale(
            UUID id, UUID clientId, UUID cinemaId, BigDecimal totalAmount,
            BigDecimal claimedAmount, BigDecimal discountedAmount,
            SaleStatusType status, LocalDateTime createdAt, LocalDateTime updatedAt,
            LocalDateTime paidAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.cinemaId = cinemaId;
        this.totalAmount = totalAmount;
        this.claimedAmount = claimedAmount;
        this.discountedAmount = discountedAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paidAt = paidAt;
    }

    public Sale(Sale sale) {
        this.id = sale.id;
        this.clientId = sale.clientId;
        this.cinemaId = sale.cinemaId;
        this.claimedAmount = sale.claimedAmount;
        this.discountedAmount = sale.discountedAmount;
        this.totalAmount = sale.totalAmount;
        this.status = sale.status;
        this.createdAt = sale.createdAt;
        this.updatedAt = sale.updatedAt;
        this.paidAt = sale.paidAt;
    }

    /**
     * Constructor for creating a new Sale
     *
     * @param clientId
     * @param cinemaId
     * @param saleLineSnacks
     * @param saleLineTickets
     */
    public Sale(UUID clientId, UUID cinemaId, BigDecimal discountedAmount, List<SaleLineSnack> saleLineSnacks, List<SaleLineTicket> saleLineTickets) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.cinemaId = cinemaId;
        var amountFromTickets = saleLineTickets.stream()
                .map(SaleLineTicket::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var amountFromSnacks = saleLineSnacks.stream()
                .map(SaleLineSnack::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = amountFromTickets.add(amountFromSnacks);
        this.claimedAmount = BigDecimal.ZERO;
        this.discountedAmount = discountedAmount;
        this.saleLineSnacks = saleLineSnacks;
        this.saleLineTickets = saleLineTickets;
        this.status = SaleStatusType.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paidAt = null;
        // Assign sale ID to lines
        assignSaleIdToLines();
        // Validate sale
        validate();
    }

    /**
     * Calculate the payable amount after discount
     *
     * @return BigDecimal payable amount
     */
    public BigDecimal getPayableAmount() {
        return this.totalAmount.subtract(this.discountedAmount);
    }

    public void sumClaimedAmount(BigDecimal amount) {
        this.claimedAmount = this.claimedAmount.add(amount);
        this.updatedAt = LocalDateTime.now();
        if (this.claimedAmount.compareTo(this.totalAmount) > 0) {
            throw new IllegalArgumentException("Claimed amount cannot be greater than total amount");
        }
    }

    private void assignSaleIdToLines() {
        for (SaleLineSnack line : saleLineSnacks) {
            line.setSaleId(this.id);
        }
        for (SaleLineTicket line : saleLineTickets) {
            line.setSaleId(this.id);
        }
    }

    public void validate() {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (discountedAmount == null || discountedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discounted amount must be non-negative");
        }
        if (discountedAmount.compareTo(totalAmount) > 0) {
            throw new IllegalArgumentException("Discounted amount cannot be greater than total amount");
        }
        if (claimedAmount == null || claimedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Claimed amount must be non-negative");
        }
        if (claimedAmount.compareTo(totalAmount) > 0) {
            throw new IllegalArgumentException("Claimed amount cannot be greater than total amount");
        }
        for (SaleLineSnack line : saleLineSnacks) {
            line.validate();
        }
        for (SaleLineTicket line : saleLineTickets) {
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
        if (!(status == SaleStatusType.PENDING || status == SaleStatusType.PAID_ERROR)) {
            throw new IllegalStateException("Only pending or paid error sales can be cancelled");
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
