package com.sap.sales_service.sale.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SnackView(
        UUID id,
        String name,
        BigDecimal price,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
