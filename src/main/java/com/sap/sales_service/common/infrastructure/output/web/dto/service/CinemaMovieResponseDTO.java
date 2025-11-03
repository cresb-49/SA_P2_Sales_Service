package com.sap.sales_service.common.infrastructure.output.web.dto.service;

import java.util.UUID;

public record CinemaMovieResponseDTO(
        UUID id,
        CinemaResponseDTO cinema,
        UUID movieId,
        Boolean active
) {
}
