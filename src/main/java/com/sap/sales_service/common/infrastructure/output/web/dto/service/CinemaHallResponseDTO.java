package com.sap.sales_service.common.infrastructure.output.web.dto.service;

import java.util.UUID;

public record CinemaHallResponseDTO(
        UUID id,
        CinemaResponseDTO cinema,
        String name,
        Integer columns,
        Integer rows,
        Boolean acceptComments,
        Boolean visible
) {
}
