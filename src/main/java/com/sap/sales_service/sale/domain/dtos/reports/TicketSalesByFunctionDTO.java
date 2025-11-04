package com.sap.sales_service.sale.domain.dtos.reports;

import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;

import java.util.UUID;

public record TicketSalesByFunctionDTO(
        UUID functionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID movieId,
        Long ticketsSold,
        TicketShowtimeReportView showtime,
        MovieSummaryView movie
) {
}
