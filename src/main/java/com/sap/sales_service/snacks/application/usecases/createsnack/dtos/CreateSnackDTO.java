package com.sap.sales_service.snacks.application.usecases.createsnack.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record CreateSnackDTO(
    String name,
    BigDecimal price,
    MultipartFile file
) {
}
