package com.sap.sales_service.sale.application.usecases.topcinemasales;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.TopCinemaSalesReportFactory;
import com.sap.sales_service.sale.application.input.TopCinemaSalesReportCasePort;
import com.sap.sales_service.sale.application.ouput.TopCinemaSalesReportPort;
import com.sap.sales_service.sale.application.usecases.topcinemasales.dto.TopCinemaSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopCinemaSalesReportCase implements TopCinemaSalesReportCasePort {

    private final TopCinemaSalesReportPort topCinemaSalesReportPort;
    private final TopCinemaSalesReportFactory topCinemaSalesReportFactory;
    private final JasperReportServicePort jasperReportService;

    private static final String REPORT_TITLE = "Top de Cines por Ventas";
    private static final String REPORT_TEMPLATE = "top_cinema_sales_report";

    @Override
    public TopCinemaSalesReportDTO report(LocalDate from, LocalDate to, int limit) {
        int topLimit = limit <= 0 ? 5 : limit;
        List<CinemaSalesSummaryDTO> summaries = topCinemaSalesReportPort.getTopCinemaSales(
                from.atStartOfDay(),
                to.atTime(23, 59, 59),
                topLimit
        );
        var enriched = topCinemaSalesReportFactory.withCinema(summaries);
        return new TopCinemaSalesReportDTO(
                enriched,
                from,
                to
        );
    }

    @Override
    public byte[] generateReportFile(LocalDate from, LocalDate to, int limit) {
        int topLimit = limit <= 0 ? 5 : limit;
        TopCinemaSalesReportDTO report = report(from, to, topLimit);
        var data = buildFlatData(report.cinemas());
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("limit", topLimit);
        return jasperReportService.toPdf(REPORT_TEMPLATE, data, params);
    }

    private List<Map<String, Object>> buildFlatData(List<CinemaSalesSummaryDTO> cinemas) {
        if (cinemas == null) {
            return List.of();
        }
        return cinemas.stream()
                .map(summary -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("cinemaId", summary.cinemaId());
                    row.put("cinemaName", summary.cinema() == null ? null : summary.cinema().name());
                    row.put("totalAmount", summary.totalAmount());
                    row.put("totalSales", summary.totalSales() == null ? 0L : summary.totalSales());
                    return row;
                })
                .toList();
    }
}
