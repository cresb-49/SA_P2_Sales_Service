package com.sap.sales_service.sale.application.usecases.ticketsalesreport;

import com.sap.sales_service.common.infrastructure.output.jasper.port.JasperReportServicePort;
import com.sap.sales_service.sale.application.factory.TicketSalesReportFactory;
import com.sap.sales_service.sale.application.input.TicketSalesReportCasePort;
import com.sap.sales_service.sale.application.ouput.TicketSalesReportPort;
import com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto.TicketSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TicketSalesReportCase implements TicketSalesReportCasePort {

    private final TicketSalesReportPort ticketSalesReportPort;
    private final TicketSalesReportFactory ticketSalesReportFactory;
    private final JasperReportServicePort jasperReportService;

    private static final String REPORT_TITLE = "Reporte de Ventas de Boletos";
    private static final String REPORT_TEMPLATE = "ticket_sales_report";
    private static final DateTimeFormatter SHOWTIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public TicketSalesReportDTO report(LocalDate from, LocalDate to) {
        List<TicketSalesByFunctionDTO> summaries = ticketSalesReportPort.getTicketSales(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)
        );

        var enriched = ticketSalesReportFactory.attachDetails(summaries);

        Long totalTickets = enriched.stream()
                .map(TicketSalesByFunctionDTO::ticketsSold)
                .filter(java.util.Objects::nonNull)
                .reduce(0L, Long::sum);

        return new TicketSalesReportDTO(
                enriched,
                totalTickets,
                from,
                to
        );
    }

    @Override
    public byte[] generateReportFile(LocalDate from, LocalDate to) {
        TicketSalesReportDTO report = report(from, to);
        var data = buildFlatData(report.functions());
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("totalTickets", report.totalTickets() == null ? 0L : report.totalTickets());
        return jasperReportService.toPdfCompiled(REPORT_TEMPLATE, data, params);
    }

    private List<Map<String, Object>> buildFlatData(List<TicketSalesByFunctionDTO> functions) {
        if (functions == null) {
            return List.of();
        }
        return functions.stream()
                .map(function -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("movieTitle", function.movie() == null ? null : function.movie().title());
                    var showtime = function.showtime();
                    row.put("cinemaName", showtime == null || showtime.cinema() == null ? null : showtime.cinema().name());
                    row.put("hallName", showtime == null ? null : showtime.hallName());
                    row.put("showtimeStart", showtime != null && showtime.startTime() != null
                            ? showtime.startTime().format(SHOWTIME_FORMATTER)
                            : null);
                    row.put("ticketsSold", function.ticketsSold() == null ? 0L : function.ticketsSold());
                    return row;
                })
                .toList();
    }
}
