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
    public UUID getFunctionId() {
        return functionId;
    }

    public UUID getHallId() {
        return hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public CinemaView getCinema() {
        return cinema;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getTicketsAvailable() {
        return ticketsAvailable;
    }
}
