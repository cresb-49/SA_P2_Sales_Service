package com.sap.sales_service.snacks.infrastructure.input.web.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SnackResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
