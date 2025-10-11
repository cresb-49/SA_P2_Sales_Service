package com.sap.sales_service.snacks.infrastructure.input.web.dtos;

import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import com.sap.sales_service.snacks.application.usecases.updatesnack.dtos.UpdateSnackDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateSnackRequestDTO(
        String name,
        BigDecimal price
) {

    public UpdateSnackDTO toDomain(UUID id,MultipartFile file) {
        return new UpdateSnackDTO(
                id,
                name,
                price,
                file
        );
    }
}
