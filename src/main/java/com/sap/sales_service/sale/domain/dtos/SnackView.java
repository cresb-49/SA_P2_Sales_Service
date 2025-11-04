package com.sap.sales_service.sale.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SnackView(
        UUID id,
        UUID cinemaId,
        String name,
        BigDecimal price,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public UUID getId() {
        return id;
    }

    public UUID getCinemaId() {
        return cinemaId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
