package com.sap.sales_service.sale.application.usecases.ticketsalesreport;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.TicketSalesReportFactory;
import com.sap.sales_service.sale.application.ouput.TicketSalesReportPort;
import com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto.TicketSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketSalesReportCaseTest {

    private static final LocalDate FROM = LocalDate.of(2024, 3, 1);
    private static final LocalDate TO = LocalDate.of(2024, 3, 31);
    private static final UUID FUNCTION_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_FUNCTION_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID MOVIE_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID CINEMA_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID HALL_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    @Mock
    private TicketSalesReportPort ticketSalesReportPort;

    @Mock
    private TicketSalesReportFactory ticketSalesReportFactory;

    @Mock
    private JasperReportServicePort jasperReportService;

    @InjectMocks
    private TicketSalesReportCase useCase;

    private TicketSalesByFunctionDTO summary(UUID functionId, UUID movieId, Long ticketsSold) {
        return new TicketSalesByFunctionDTO(
                functionId,
                CINEMA_ID,
                HALL_ID,
                movieId,
                ticketsSold,
                new TicketShowtimeReportView(
                        functionId,
                        HALL_ID,
                        "Sala 1",
                        new CinemaView(CINEMA_ID, "Cinema UX"),
                        LocalDateTime.of(2024, 3, 10, 18, 0),
                        LocalDateTime.of(2024, 3, 10, 20, 0),
                        100
                ),
                movieId != null ? new MovieSummaryView(movieId, "Matrix") : null
        );
    }

    @Test
    void report_shouldAggregateTicketsAndReturnDto() {
        // Arrange
        var rawSummaries = List.of(
                new TicketSalesByFunctionDTO(FUNCTION_ID, CINEMA_ID, HALL_ID, MOVIE_ID, 15L, null, null),
                new TicketSalesByFunctionDTO(OTHER_FUNCTION_ID, CINEMA_ID, HALL_ID, null, 5L, null, null)
        );
        var enriched = List.of(
                summary(FUNCTION_ID, MOVIE_ID, 15L),
                summary(OTHER_FUNCTION_ID, null, 5L)
        );

        when(ticketSalesReportPort.getTicketSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59))
        )).thenReturn(rawSummaries);
        when(ticketSalesReportFactory.attachDetails(rawSummaries)).thenReturn(enriched);

        // Act
        TicketSalesReportDTO result = useCase.report(FROM, TO);

        // Assert
        assertThat(result.functions()).isEqualTo(enriched);
        assertThat(result.totalTickets()).isEqualTo(20L);
        assertThat(result.from()).isEqualTo(FROM);
        assertThat(result.to()).isEqualTo(TO);
        verify(ticketSalesReportPort).getTicketSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59))
        );
        verify(ticketSalesReportFactory).attachDetails(rawSummaries);
    }

    @Test
    void generateReportFile_shouldCallJasperWithExpectedArguments() {
        // Arrange
        var rawSummaries = List.of(
                new TicketSalesByFunctionDTO(FUNCTION_ID, CINEMA_ID, HALL_ID, MOVIE_ID, 15L, null, null)
        );
        var enriched = List.of(
                summary(FUNCTION_ID, MOVIE_ID, 15L)
        );
        byte[] pdf = {5, 5, 5};

        when(ticketSalesReportPort.getTicketSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59))
        )).thenReturn(rawSummaries);
        when(ticketSalesReportFactory.attachDetails(rawSummaries)).thenReturn(enriched);
        when(jasperReportService.toPdf(eq("ticket_sales_report"), eq(enriched), org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(pdf);

        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        byte[] result = useCase.generateReportFile(FROM, TO);

        // Assert
        assertThat(result).isEqualTo(pdf);
        verify(jasperReportService).toPdf(eq("ticket_sales_report"), eq(enriched), paramsCaptor.capture());

        Map<String, Object> params = paramsCaptor.getValue();
        assertThat(params).containsEntry("reportTitle", "Reporte de Ventas de Boletos");
        assertThat(params).containsEntry("from", FROM);
        assertThat(params).containsEntry("to", TO);
        assertThat(params).containsEntry("totalTickets", 15L);
    }
}
