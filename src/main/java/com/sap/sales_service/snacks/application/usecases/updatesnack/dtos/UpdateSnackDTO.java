package com.sap.sales_service.snacks.application.usecases.updatesnack.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateSnackDTO(
        UUID id,
        String name,
        BigDecimal price,
        MultipartFile file
) {
}
