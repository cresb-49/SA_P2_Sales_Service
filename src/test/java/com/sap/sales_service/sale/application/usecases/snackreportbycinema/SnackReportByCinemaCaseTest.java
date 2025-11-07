package com.sap.sales_service.sale.application.usecases.snackreportbycinema;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.SnackReportByCinemaFactory;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.application.ouput.SnackReportByCinema;
import com.sap.sales_service.sale.application.usecases.snackreportbycinema.dto.SnackReportByCinemaReportDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
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
class SnackReportByCinemaCaseTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final LocalDate FROM = LocalDate.of(2024, 2, 1);
    private static final LocalDate TO = LocalDate.of(2024, 2, 28);

    @Mock
    private SnackReportByCinemaFactory snackReportByCinemaFactory;

    @Mock
    private SnackReportByCinema snackReportByCinema;

    @Mock
    private FindCinemaPort findCinemaPort;

    @Mock
    private JasperReportServicePort jasperReportService;

    @InjectMocks
    private SnackReportByCinemaCase useCase;

    private SnackSalesByCinemaDTO sale(UUID snackId, long quantity, String snackName, BigDecimal total) {
        return new SnackSalesByCinemaDTO(
                CINEMA_ID,
                snackId,
                quantity,
                total,
                new SnackView(
                        snackId,
                        CINEMA_ID,
                        snackName,
                        BigDecimal.valueOf(5),
                        "http://image.png",
                        LocalDateTime.of(2024, 2, 1, 10, 0),
                        LocalDateTime.of(2024, 2, 2, 10, 0)
                )
        );
    }

    @Test
    void report_shouldAggregateTotalsAndAttachCinema() {
        // Arrange
        var base = List.of(
                new SnackSalesByCinemaDTO(CINEMA_ID, UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), 4L, new BigDecimal("40.00"), null),
                new SnackSalesByCinemaDTO(CINEMA_ID, UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), 6L, new BigDecimal("60.00"), null)
        );
        var enriched = List.of(
                sale(base.getFirst().snackId(), 4L, "Pop Corn", new BigDecimal("40.00")),
                sale(base.get(1).snackId(), 6L, "Hot Dog", new BigDecimal("60.00"))
        );
        var cinema = new CinemaView(CINEMA_ID, "Cinema UX");

        when(snackReportByCinema.getSnackSalesByCinemaDTOs(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        )).thenReturn(base);
        when(snackReportByCinemaFactory.withSnackView(base)).thenReturn(enriched);
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema);

        // Act
        SnackReportByCinemaReportDTO result = useCase.report(FROM, TO, CINEMA_ID);

        // Assert
        assertThat(result.snackSalesByCinemaDTOs()).isEqualTo(enriched);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.cinema()).isEqualTo(cinema);
        assertThat(result.from()).isEqualTo(FROM);
        assertThat(result.to()).isEqualTo(TO);
        verify(snackReportByCinema).getSnackSalesByCinemaDTOs(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        );
        verify(snackReportByCinemaFactory).withSnackView(base);
        verify(findCinemaPort).findById(CINEMA_ID);
    }

    @Test
    void generateReportFile_shouldUseJasperWithExpectedParameters() {
        // Arrange
        var base = List.of(
                new SnackSalesByCinemaDTO(CINEMA_ID, UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), 2L, new BigDecimal("20.00"), null)
        );
        var enriched = List.of(
                sale(base.getFirst().snackId(), 2L, "Pop Corn", new BigDecimal("20.00"))
        );
        var cinema = new CinemaView(CINEMA_ID, "Cinema UX");
        byte[] pdf = {9, 9, 9};

        when(snackReportByCinema.getSnackSalesByCinemaDTOs(
                FROM.atStartOfDay(),
                TO.atTime(LocalTime.of(23, 59, 59)),
                CINEMA_ID
        )).thenReturn(base);
        when(snackReportByCinemaFactory.withSnackView(base)).thenReturn(enriched);
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema);
        when(jasperReportService.toPdf(
                eq("snack_report_by_cinema"),
                org.mockito.ArgumentMatchers.anyCollection(),
                org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(pdf);

        ArgumentCaptor<Collection<Map<String, ?>>> dataCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        // Act
        byte[] result = useCase.generateReportFile(FROM, TO, CINEMA_ID);

        // Assert
        assertThat(result).isEqualTo(pdf);
        verify(jasperReportService).toPdf(eq("snack_report_by_cinema"), dataCaptor.capture(), paramsCaptor.capture());

        Map<String, Object> params = paramsCaptor.getValue();
        assertThat(params).containsEntry("reportTitle", "Ventas de Snacks por Cine");
        assertThat(params).containsEntry("from", FROM);
        assertThat(params).containsEntry("to", TO);
        assertThat(params).containsEntry("cinema", cinema.name());
        assertThat(params).containsEntry("totalAmount", new BigDecimal("20.00"));

        Collection<Map<String, ?>> data = dataCaptor.getValue();
        assertThat(data).hasSize(1);
        Map<String, ?> row = data.iterator().next();
        assertThat(row.get("cinemaId")).isEqualTo(CINEMA_ID);
        assertThat(row.get("snackId")).isEqualTo(enriched.getFirst().snackId());
        assertThat(row.get("snackName")).isEqualTo(enriched.getFirst().snack().name());
        assertThat(row.get("totalQuantity")).isEqualTo(2L);
        assertThat(row.get("totalAmount")).isEqualTo(new BigDecimal("20.00"));
    }
}
