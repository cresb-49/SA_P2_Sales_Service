package com.sap.sales_service.sale.application.usecases.topcinemasales;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.TopCinemaSalesReportFactory;
import com.sap.sales_service.sale.application.ouput.TopCinemaSalesReportPort;
import com.sap.sales_service.sale.application.usecases.topcinemasales.dto.TopCinemaSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
class TopCinemaSalesReportCaseTest {

    private static final LocalDate FROM = LocalDate.of(2024, 4, 1);
    private static final LocalDate TO = LocalDate.of(2024, 4, 30);
    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Mock
    private TopCinemaSalesReportPort topCinemaSalesReportPort;

    @Mock
    private TopCinemaSalesReportFactory topCinemaSalesReportFactory;

    @Mock
    private JasperReportServicePort jasperReportService;

    @InjectMocks
    private TopCinemaSalesReportCase useCase;

    private CinemaSalesSummaryDTO summary(UUID cinemaId, BigDecimal totalAmount, long totalSales) {
        return new CinemaSalesSummaryDTO(
                cinemaId,
                totalAmount,
                totalSales,
                new CinemaView(cinemaId, "Cinema UX")
        );
    }

    @Test
    void report_shouldUseDefaultLimitWhenNonPositive() {
        // Arrange
        var raw = List.of(
                new CinemaSalesSummaryDTO(CINEMA_ID, new BigDecimal("150.00"), 12L, null)
        );
        var enriched = List.of(
                summary(CINEMA_ID, new BigDecimal("150.00"), 12L)
        );

        when(topCinemaSalesReportPort.getTopCinemaSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                5
        )).thenReturn(raw);
        when(topCinemaSalesReportFactory.withCinema(raw)).thenReturn(enriched);

        // Act
        TopCinemaSalesReportDTO result = useCase.report(FROM, TO, 0);

        // Assert
        assertThat(result.cinemas()).isEqualTo(enriched);
        assertThat(result.from()).isEqualTo(FROM);
        assertThat(result.to()).isEqualTo(TO);
        verify(topCinemaSalesReportPort).getTopCinemaSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                5
        );
        verify(topCinemaSalesReportFactory).withCinema(raw);
    }

    @Test
    void generateReportFile_shouldCallJasperWithLimitParameter() {
        // Arrange
        var raw = List.of(
                new CinemaSalesSummaryDTO(CINEMA_ID, new BigDecimal("200.00"), 15L, null)
        );
        var enriched = List.of(
                summary(CINEMA_ID, new BigDecimal("200.00"), 15L)
        );
        byte[] pdf = {7, 7, 7};

        when(topCinemaSalesReportPort.getTopCinemaSales(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                3
        )).thenReturn(raw);
        when(topCinemaSalesReportFactory.withCinema(raw)).thenReturn(enriched);
        when(jasperReportService.toPdfCompiled(
                eq("top_cinema_sales_report"),
                org.mockito.ArgumentMatchers.anyCollection(),
                org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(pdf);

        ArgumentCaptor<Collection<Map<String, ?>>> dataCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        byte[] result = useCase.generateReportFile(FROM, TO, 3);

        // Assert
        assertThat(result).isEqualTo(pdf);
        verify(jasperReportService).toPdfCompiled(eq("top_cinema_sales_report"), dataCaptor.capture(), paramsCaptor.capture());
        Map<String, Object> params = paramsCaptor.getValue();
        assertThat(params).containsEntry("reportTitle", "Top de Cines por Ventas");
        assertThat(params).containsEntry("from", FROM);
        assertThat(params).containsEntry("to", TO);
        assertThat(params).containsEntry("limit", 3);

        Collection<Map<String, ?>> data = dataCaptor.getValue();
        assertThat(data).hasSize(1);
        Map<String, ?> row = data.iterator().next();
        assertThat(row.get("cinemaId")).isEqualTo(CINEMA_ID);
        assertThat(row.get("cinemaName")).isEqualTo("Cinema UX");
        assertThat(row.get("totalAmount")).isEqualTo(new BigDecimal("200.00"));
        assertThat(row.get("totalSales")).isEqualTo(15L);
    }
}
