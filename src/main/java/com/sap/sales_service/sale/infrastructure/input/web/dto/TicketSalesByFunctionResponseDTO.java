package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.util.UUID;

public record TicketSalesByFunctionResponseDTO(
        UUID functionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID movieId,
        Long ticketsSold,
        TicketShowtimeReportResponseDTO showtime,
        MovieSummaryResponseDTO movie
) {
}
