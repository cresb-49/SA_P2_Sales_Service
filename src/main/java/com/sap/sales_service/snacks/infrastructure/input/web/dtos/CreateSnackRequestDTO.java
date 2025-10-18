package com.sap.sales_service.snacks.infrastructure.input.web.dtos;

import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSnackRequestDTO(
        String name,
        BigDecimal price,
        UUID cinemaId,
        String urlImage
) {

    public CreateSnackDTO toDomain(MultipartFile file) {
        return new CreateSnackDTO(
                cinemaId,
                name,
                urlImage,
                price,
                file
        );
    }
}
