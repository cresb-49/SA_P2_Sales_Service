package com.sap.sales_service.sale.application.usecases.snacksalesreport;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.SnackSalesReportFactory;
import com.sap.sales_service.sale.application.input.SnackSalesReportCasePort;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.application.ouput.SnackSalesReportPort;
import com.sap.sales_service.sale.application.usecases.snacksalesreport.dto.SnackSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackSalesReportCase implements SnackSalesReportCasePort {

    private final SnackSalesReportPort snackSalesReportPort;
    private final SnackSalesReportFactory snackSalesReportFactory;
    private final JasperReportServicePort jasperReportService;
    private final FindCinemaPort findCinemaPort;

    private static final String REPORT_TITLE = "Reporte de Ventas de Snacks";
    private static final String REPORT_TEMPLATE = "snack_sales_report";

    @Override
    public SnackSalesReportDTO report(LocalDate from, LocalDate to, UUID cinemaId) {
        List<SnackSalesSummaryDTO> summary = snackSalesReportPort.getSnackSalesSummary(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                cinemaId
        );
        var enrichedSummary = snackSalesReportFactory.withSnackView(summary);
        Long totalQuantity = enrichedSummary.stream()
                .map(SnackSalesSummaryDTO::totalQuantity)
                .filter(java.util.Objects::nonNull)
                .reduce(0L, Long::sum);
        var cinema = findCinemaPort.findById(cinemaId);

        return new SnackSalesReportDTO(
                enrichedSummary,
                totalQuantity,
                cinema,
                cinemaId,
                from,
                to
        );
    }

    @Override
    public byte[] generateReportFile(LocalDate from, LocalDate to, UUID cinemaId) {
        SnackSalesReportDTO report = report(from, to, cinemaId);
        var data = report.snacks();
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("cinema", report.cinema() == null ? "Todos" : report.cinema().name());
        params.put("totalQuantity", report.totalQuantity() == null ? 0L : report.totalQuantity());
        return jasperReportService.toPdf(REPORT_TEMPLATE, data, params);
    }
}
