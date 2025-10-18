package com.sap.sales_service.snacks.infrastructure.input.web.dtos;

import com.sap.sales_service.snacks.application.usecases.updatesnack.dtos.UpdateSnackDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateSnackRequestDTO(
        String name,
        BigDecimal price,
        String urlImage
) {

    public UpdateSnackDTO toDomain(UUID id, MultipartFile file) {
        return new UpdateSnackDTO(
                id,
                name,
                price,
                urlImage,
                file
        );
    }
}
