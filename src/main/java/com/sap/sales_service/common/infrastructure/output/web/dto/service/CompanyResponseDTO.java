package com.sap.sales_service.common.infrastructure.output.web.dto.service;

import java.util.UUID;

public record CompanyResponseDTO(
        UUID id,
        String name,
        String address,
        String phoneNumber
) {
}
