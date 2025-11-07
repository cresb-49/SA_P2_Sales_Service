package com.sap.sales_service.sale.application.usecases.snackreportbycinema;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.SnackReportByCinemaFactory;
import com.sap.sales_service.sale.application.input.SnackReportByCinemaCasePort;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.application.ouput.SnackReportByCinema;
import com.sap.sales_service.sale.application.usecases.snackreportbycinema.dto.SnackReportByCinemaReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackReportByCinemaCase implements SnackReportByCinemaCasePort {

    private final SnackReportByCinemaFactory snackReportByCinemaFactory;
    private final SnackReportByCinema snackReportByCinema;
    private final FindCinemaPort findCinemaPort;
    private final JasperReportServicePort jasperReportService;

    private static final String REPORT_TITLE = "Ventas de Snacks por Cine";
    private static final String REPORT_TEMPLATE = "snack_report_by_cinema";

    @Override
    public SnackReportByCinemaReportDTO report(LocalDate from, LocalDate to, UUID cinemaId) {
        // Get Information to from init day, to end day
        var result = snackReportByCinema.getSnackSalesByCinemaDTOs(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                cinemaId
        );
        var resultWithSnackView = snackReportByCinemaFactory.withSnackView(result);
        // find cinema
        var cinema = findCinemaPort.findById(cinemaId);
        // calculate total
        BigDecimal total = resultWithSnackView.stream()
                .map(SnackSalesByCinemaDTO::totalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SnackReportByCinemaReportDTO(
                resultWithSnackView,
                total,
                cinema,
                from,
                to
        );
    }

    @Override
    public byte[] generateReportFile(LocalDate from, LocalDate to, UUID cinemaId) {
        SnackReportByCinemaReportDTO report = report(from, to, cinemaId);
        var data = buildFlatData(report.snackSalesByCinemaDTOs());
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("cinema", report.cinema() == null ? "Desconocido" : report.cinema().name());
        params.put("totalAmount", report.totalAmount() == null ? java.math.BigDecimal.ZERO : report.totalAmount());
        return jasperReportService.toPdf(REPORT_TEMPLATE, data, params);
    }

    private List<Map<String, Object>> buildFlatData(List<SnackSalesByCinemaDTO> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("cinemaId", item.cinemaId());
                    row.put("snackId", item.snackId());
                    row.put("totalQuantity", item.totalQuantity() == null ? 0L : item.totalQuantity());
                    row.put("totalAmount", item.totalAmount());
                    row.put("snackName", item.snack() == null ? null : item.snack().name());
                    return row;
                })
                .toList();
    }
}
