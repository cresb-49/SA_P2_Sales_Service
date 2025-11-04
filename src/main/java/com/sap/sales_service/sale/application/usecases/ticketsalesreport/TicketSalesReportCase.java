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
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TicketSalesReportCase implements TicketSalesReportCasePort {

    private final TicketSalesReportPort ticketSalesReportPort;
    private final TicketSalesReportFactory ticketSalesReportFactory;
    private final JasperReportServicePort jasperReportService;

    private static final String REPORT_TITLE = "Reporte de Ventas de Boletos";
    private static final String REPORT_TEMPLATE = "ticket_sales_report";

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
        var params = new HashMap<String, Object>();
        params.put("reportTitle", REPORT_TITLE);
        params.put("from", from);
        params.put("to", to);
        params.put("totalTickets", report.totalTickets() == null ? 0L : report.totalTickets());
        return jasperReportService.toPdf(REPORT_TEMPLATE, report.functions(), params);
    }
}
