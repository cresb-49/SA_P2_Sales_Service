package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaHallResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaMovieResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CompanyResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.TicketShowtimeReportMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketShowtimeReportAdapterTest {

    private static final UUID FUNCTION_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_FUNCTION_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID CINEMA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID HALL_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    @Mock
    private CinemaGatewayPort cinemaGatewayPort;

    @Mock
    private TicketShowtimeReportMapper ticketShowtimeReportMapper;

    @InjectMocks
    private TicketShowtimeReportAdapter adapter;

    @Test
    void findShowtimesByIds_shouldReturnEmptyList_whenIdsNullOrEmpty() {
        // Act
        var resultWithNull = adapter.findShowtimesByIds(null);
        var resultWithEmpty = adapter.findShowtimesByIds(List.of());

        // Assert
        assertThat(resultWithNull).isEmpty();
        assertThat(resultWithEmpty).isEmpty();
        verify(cinemaGatewayPort, never()).findFunctionsByIds(org.mockito.ArgumentMatchers.anyList());
        verify(ticketShowtimeReportMapper, never()).toViewList(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void findShowtimesByIds_shouldDelegateToGatewayAndMapper() {
        // Arrange
        List<UUID> ids = List.of(FUNCTION_ID, OTHER_FUNCTION_ID);
        List<ShowtimeResponseDTO> dtos = List.of(
                showtime(FUNCTION_ID),
                showtime(OTHER_FUNCTION_ID)
        );
        List<TicketShowtimeReportView> expected = List.of(
                view(FUNCTION_ID),
                view(OTHER_FUNCTION_ID)
        );

        when(cinemaGatewayPort.findFunctionsByIds(ids)).thenReturn(dtos);
        when(ticketShowtimeReportMapper.toViewList(dtos)).thenReturn(expected);

        // Act
        var result = adapter.findShowtimesByIds(ids);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(cinemaGatewayPort).findFunctionsByIds(ids);
        verify(ticketShowtimeReportMapper).toViewList(dtos);
    }

    private ShowtimeResponseDTO showtime(UUID id) {
        var company = new CompanyResponseDTO(UUID.randomUUID(), "Company", "Street 123", "555-1234");
        var cinema = new CinemaResponseDTO(CINEMA_ID, company, "Cinema UX", BigDecimal.TEN, LocalDate.of(2020, 1, 1));
        var hall = new CinemaHallResponseDTO(HALL_ID, cinema, "Sala 1", 10, 10, true, true);
        var cinemaMovie = new CinemaMovieResponseDTO(UUID.randomUUID(), cinema, UUID.randomUUID(), true);
        return new ShowtimeResponseDTO(
                id,
                cinemaMovie,
                hall,
                LocalDateTime.of(2024, 5, 1, 18, 0),
                LocalDateTime.of(2024, 5, 1, 20, 0),
                100,
                BigDecimal.valueOf(8.5)
        );
    }

    private TicketShowtimeReportView view(UUID id) {
        return new TicketShowtimeReportView(
                id,
                HALL_ID,
                "Sala 1",
                new com.sap.sales_service.sale.domain.dtos.CinemaView(CINEMA_ID, "Cinema UX"),
                LocalDateTime.of(2024, 5, 1, 18, 0),
                LocalDateTime.of(2024, 5, 1, 20, 0),
                100
        );
    }
}
