package com.sap.sales_service.snacks.infrastructure.input.service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SnackInternalView(
        UUID id,
        String name,
        BigDecimal price,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
