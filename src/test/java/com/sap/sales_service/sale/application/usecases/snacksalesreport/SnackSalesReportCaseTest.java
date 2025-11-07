package com.sap.sales_service.sale.application.usecases.snacksalesreport;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.SnackSalesReportFactory;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.application.ouput.SnackSalesReportPort;
import com.sap.sales_service.sale.application.usecases.snacksalesreport.dto.SnackSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnackSalesReportCaseTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2024, 1, 31);

    @Mock
    private SnackSalesReportPort snackSalesReportPort;

    @Mock
    private SnackSalesReportFactory snackSalesReportFactory;

    @Mock
    private JasperReportServicePort jasperReportService;

    @Mock
    private FindCinemaPort findCinemaPort;

    @InjectMocks
    private SnackSalesReportCase useCase;

    private SnackView snackView(UUID id, String name) {
        return new SnackView(
                id,
                CINEMA_ID,
                name,
                new BigDecimal("5.00"),
                "http://image.png",
                LocalDateTime.of(2024, 1, 1, 9, 0),
                LocalDateTime.of(2024, 1, 2, 9, 0)
        );
    }

    @Test
    void report_shouldReturnEnrichedSummaryWithTotalsAndCinema() {
        // Arrange
        var summary = List.of(
                new SnackSalesSummaryDTO(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), 10L, null),
                new SnackSalesSummaryDTO(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), 5L, null)
        );
        var enriched = List.of(
                new SnackSalesSummaryDTO(summary.getFirst().snackId(), 10L, snackView(summary.getFirst().snackId(), "Pop Corn")),
                new SnackSalesSummaryDTO(summary.get(1).snackId(), 5L, snackView(summary.get(1).snackId(), "Hot Dog"))
        );
        var cinema = new CinemaView(CINEMA_ID, "Cinema UX");

        when(snackSalesReportPort.getSnackSalesSummary(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        )).thenReturn(summary);
        when(snackSalesReportFactory.withSnackView(summary)).thenReturn(enriched);
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema);

        // Act
        SnackSalesReportDTO result = useCase.report(FROM, TO, CINEMA_ID);

        // Assert
        assertThat(result.snacks()).isEqualTo(enriched);
        assertThat(result.totalQuantity()).isEqualTo(15L);
        assertThat(result.cinema()).isEqualTo(cinema);
        assertThat(result.cinemaId()).isEqualTo(CINEMA_ID);
        assertThat(result.from()).isEqualTo(FROM);
        assertThat(result.to()).isEqualTo(TO);

        verify(snackSalesReportPort).getSnackSalesSummary(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        );
        verify(snackSalesReportFactory).withSnackView(summary);
        verify(findCinemaPort).findById(CINEMA_ID);
    }

    @Test
    void generateReportFile_shouldCallJasperWithExpectedTemplateAndParams() {
        // Arrange
        var summary = List.of(
                new SnackSalesSummaryDTO(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), 3L, null)
        );
        var enriched = List.of(
                new SnackSalesSummaryDTO(summary.getFirst().snackId(), 3L, snackView(summary.getFirst().snackId(), "Pop Corn"))
        );
        var cinema = new CinemaView(CINEMA_ID, "Cinema UX");
        byte[] pdf = {1, 2, 3};

        when(snackSalesReportPort.getSnackSalesSummary(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        )).thenReturn(summary);
        when(snackSalesReportFactory.withSnackView(summary)).thenReturn(enriched);
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema);
        when(jasperReportService.toPdfCompiled(
                eq("snack_sales_report"),
                org.mockito.ArgumentMatchers.anyCollection(),
                org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(pdf);

        ArgumentCaptor<Collection<Map<String, ?>>> dataCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        byte[] result = useCase.generateReportFile(FROM, TO, CINEMA_ID);

        // Assert
        assertThat(result).isEqualTo(pdf);
        verify(jasperReportService).toPdfCompiled(eq("snack_sales_report"), dataCaptor.capture(), paramsCaptor.capture());

        Map<String, Object> params = paramsCaptor.getValue();
        assertThat(params).containsEntry("reportTitle", "Reporte de Ventas de Snacks");
        assertThat(params).containsEntry("from", FROM);
        assertThat(params).containsEntry("to", TO);
        assertThat(params).containsEntry("cinema", cinema.name());
        assertThat(params).containsEntry("totalQuantity", 3L);

        Collection<Map<String, ?>> data = dataCaptor.getValue();
        assertThat(data).hasSize(1);
        Map<String, ?> row = data.iterator().next();
        BigDecimal unitPrice = enriched.getFirst().snack().price();
        Long quantity = enriched.getFirst().totalQuantity();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        assertThat(row.get("snackId")).isEqualTo(enriched.getFirst().snackId());
        assertThat(row.get("snackName")).isEqualTo(enriched.getFirst().snack().name());
        assertThat(row.get("unitPrice")).isEqualTo(unitPrice);
        assertThat(row.get("totalQuantity")).isEqualTo(quantity);
        assertThat(row.get("lineTotal")).isEqualTo(lineTotal);
    }
}
