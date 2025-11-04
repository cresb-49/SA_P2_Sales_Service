package com.sap.sales_service.sale.domain.dtos.reports.views;

import com.sap.sales_service.sale.domain.dtos.CinemaView;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketShowtimeReportView(
        UUID functionId,
        UUID hallId,
        String hallName,
        CinemaView cinema,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer ticketsAvailable
) {
}
