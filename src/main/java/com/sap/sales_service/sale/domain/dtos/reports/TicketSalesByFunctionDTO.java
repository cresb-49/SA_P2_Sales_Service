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
    public UUID getFunctionId() {
        return functionId;
    }

    public UUID getCinemaId() {
        return cinemaId;
    }

    public UUID getCinemaRoomId() {
        return cinemaRoomId;
    }

    public UUID getMovieId() {
        return movieId;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public TicketShowtimeReportView getShowtime() {
        return showtime;
    }

    public MovieSummaryView getMovie() {
        return movie;
    }
}
