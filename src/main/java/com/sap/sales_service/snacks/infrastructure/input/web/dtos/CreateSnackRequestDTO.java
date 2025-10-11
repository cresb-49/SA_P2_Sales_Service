package com.sap.sales_service.snacks.infrastructure.input.web.dtos;

import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateSnackRequestDTO(
        String name,
        BigDecimal price
) {

    public CreateSnackDTO toDomain(MultipartFile file) {
        return new CreateSnackDTO(
                name,
                price,
                file
        );
    }
}
