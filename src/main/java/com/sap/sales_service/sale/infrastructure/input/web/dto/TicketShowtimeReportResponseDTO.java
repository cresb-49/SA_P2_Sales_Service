package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketShowtimeReportResponseDTO(
        UUID hallId,
        String hallName,
        CinemaSummaryResponseDTO cinema,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer ticketsAvailable
) {
}
