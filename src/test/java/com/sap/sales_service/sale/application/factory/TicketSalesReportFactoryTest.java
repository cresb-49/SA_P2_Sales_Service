package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.TicketMovieReportPort;
import com.sap.sales_service.sale.application.ouput.TicketShowtimeReportPort;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketSalesReportFactoryTest {

    private static final UUID FUNCTION_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_FUNCTION_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID MOVIE_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID CINEMA_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID HALL_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    @Mock
    private TicketShowtimeReportPort ticketShowtimeReportPort;

    @Mock
    private TicketMovieReportPort ticketMovieReportPort;

    @InjectMocks
    private TicketSalesReportFactory factory;

    private TicketSalesByFunctionDTO summary(UUID functionId, UUID movieId, Long tickets) {
        return new TicketSalesByFunctionDTO(
                functionId,
                CINEMA_ID,
                HALL_ID,
                movieId,
                tickets,
                null,
                null
        );
    }

    private TicketShowtimeReportView showtime(UUID functionId) {
        return new TicketShowtimeReportView(
                functionId,
                HALL_ID,
                "Sala 1",
                new CinemaView(CINEMA_ID, "Cinema UX"),
                LocalDateTime.of(2024, 1, 1, 18, 0),
                LocalDateTime.of(2024, 1, 1, 20, 0),
                100
        );
    }

    private MovieSummaryView movie(UUID movieId) {
        return new MovieSummaryView(movieId, "Matrix");
    }

    @Test
    void attachDetails_shouldReturnSameList_whenSummariesEmpty() {
        // Arrange
        var summaries = List.<TicketSalesByFunctionDTO>of();

        // Act
        var result = factory.attachDetails(summaries);

        // Assert
        assertThat(result).isSameAs(summaries);
        verifyNoInteractions(ticketShowtimeReportPort, ticketMovieReportPort);
    }

    @Test
    void attachDetails_shouldEnrichSummaries() {
        // Arrange
        var summaryWithMovie = summary(FUNCTION_ID, MOVIE_ID, 25L);
        var summaryWithoutMovie = summary(OTHER_FUNCTION_ID, null, 10L);
        var showtime1 = showtime(FUNCTION_ID);
        var showtime2 = showtime(OTHER_FUNCTION_ID);
        var movie = movie(MOVIE_ID);

        when(ticketShowtimeReportPort.findShowtimesByIds(List.of(FUNCTION_ID, OTHER_FUNCTION_ID))).thenReturn(List.of(showtime1, showtime2));
        when(ticketMovieReportPort.findMoviesByIds(List.of(MOVIE_ID))).thenReturn(List.of(movie));

        // Act
        var result = factory.attachDetails(List.of(summaryWithMovie, summaryWithoutMovie));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().showtime()).isEqualTo(showtime1);
        assertThat(result.getFirst().movie()).isEqualTo(movie);
        assertThat(result.get(1).showtime()).isEqualTo(showtime2);
        assertThat(result.get(1).movie()).isNull();

        verify(ticketShowtimeReportPort).findShowtimesByIds(argThat(list ->
                list.containsAll(List.of(FUNCTION_ID, OTHER_FUNCTION_ID)) && list.size() == 2
        ));

        verify(ticketMovieReportPort).findMoviesByIds(argThat(list ->
                list.size() == 1 && list.contains(MOVIE_ID)
        ));
    }
}
