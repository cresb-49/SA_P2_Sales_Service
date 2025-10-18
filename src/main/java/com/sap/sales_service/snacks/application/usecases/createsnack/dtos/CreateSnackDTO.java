package com.sap.sales_service.snacks.application.usecases.createsnack.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSnackDTO(
        UUID cinemaId,
        String name,
        String urlImage,
        BigDecimal price,
        MultipartFile file
) {
}
