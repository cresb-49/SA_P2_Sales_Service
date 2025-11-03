package com.sap.sales_service.common.infrastructure.output.web.dto.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShowtimeResponseDTO(
        UUID id,
        CinemaMovieResponseDTO cinemaMovie,
        CinemaHallResponseDTO hall,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer ticketsAvailable,
        BigDecimal price
) {
}
