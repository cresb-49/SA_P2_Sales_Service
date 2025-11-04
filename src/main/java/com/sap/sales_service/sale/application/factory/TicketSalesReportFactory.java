package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.TicketMovieReportPort;
import com.sap.sales_service.sale.application.ouput.TicketShowtimeReportPort;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TicketSalesReportFactory {

    private final TicketShowtimeReportPort ticketShowtimeReportPort;
    private final TicketMovieReportPort ticketMovieReportPort;

    public List<TicketSalesByFunctionDTO> attachDetails(List<TicketSalesByFunctionDTO> summaries) {
        if (summaries.isEmpty()) {
            return summaries;
        }

        var functionIds = summaries.stream()
                .map(TicketSalesByFunctionDTO::functionId)
                .collect(Collectors.toSet());
        var movieIds = summaries.stream()
                .map(TicketSalesByFunctionDTO::movieId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<UUID, TicketShowtimeReportView> showtimeMap = ticketShowtimeReportPort.findShowtimesByIds(
                List.copyOf(functionIds)
        ).stream().collect(Collectors.toMap(
                TicketShowtimeReportView::functionId,
                view -> view,
                (first, second) -> first
        ));

        Map<UUID, MovieSummaryView> movieMap = movieIds.isEmpty()
                ? Map.of()
                : ticketMovieReportPort.findMoviesByIds(List.copyOf(movieIds)).stream()
                .collect(Collectors.toMap(
                        MovieSummaryView::id,
                        view -> view,
                        (first, second) -> first
                ));

        return summaries.stream()
                .map(summary -> new TicketSalesByFunctionDTO(
                        summary.functionId(),
                        summary.cinemaId(),
                        summary.cinemaRoomId(),
                        summary.movieId(),
                        summary.ticketsSold(),
                        showtimeMap.get(summary.functionId()),
                        summary.movieId() != null ? movieMap.get(summary.movieId()) : null
                ))
                .toList();
    }
}
