package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto.TicketSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import com.sap.sales_service.sale.infrastructure.input.web.dto.CinemaSummaryResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.MovieSummaryResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.TicketSalesByFunctionResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.TicketSalesReportResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.TicketShowtimeReportResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TicketSalesReportResponseMapper {

    public TicketSalesReportResponseDTO toResponseDTO(TicketSalesReportDTO dto) {
        return new TicketSalesReportResponseDTO(
                dto.from(),
                dto.to(),
                dto.totalTickets(),
                dto.functions().stream()
                        .map(this::toFunctionResponse)
                        .toList()
        );
    }

    private TicketSalesByFunctionResponseDTO toFunctionResponse(TicketSalesByFunctionDTO dto) {
        return new TicketSalesByFunctionResponseDTO(
                dto.functionId(),
                dto.cinemaId(),
                dto.cinemaRoomId(),
                dto.movieId(),
                dto.ticketsSold(),
                toShowtimeResponse(dto.showtime()),
                toMovieResponse(dto.movie())
        );
    }

    private TicketShowtimeReportResponseDTO toShowtimeResponse(TicketShowtimeReportView view) {
        if (view == null) {
            return null;
        }
        return new TicketShowtimeReportResponseDTO(
                view.hallId(),
                view.hallName(),
                toCinemaSummary(view.cinema()),
                view.startTime(),
                view.endTime(),
                view.ticketsAvailable()
        );
    }

    private CinemaSummaryResponseDTO toCinemaSummary(CinemaView cinemaView) {
        if (cinemaView == null) {
            return null;
        }
        return new CinemaSummaryResponseDTO(
                cinemaView.id(),
                cinemaView.name()
        );
    }

    private MovieSummaryResponseDTO toMovieResponse(MovieSummaryView view) {
        if (view == null) {
            return null;
        }
        return new MovieSummaryResponseDTO(
                view.id(),
                view.title()
        );
    }
}
