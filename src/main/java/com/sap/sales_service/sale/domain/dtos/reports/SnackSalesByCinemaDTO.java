package com.sap.sales_service.sale.domain.dtos.reports;

import com.sap.sales_service.sale.domain.dtos.SnackView;

import java.math.BigDecimal;
import java.util.UUID;

public record SnackSalesByCinemaDTO(
        UUID cinemaId,
        UUID snackId,
        Long totalQuantity,
        BigDecimal totalAmount,
        //Optional
        SnackView snack
) {
    public UUID getCinemaId() {
        return cinemaId;
    }

    public UUID getSnackId() {
        return snackId;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public SnackView getSnack() {
        return snack;
    }
}
