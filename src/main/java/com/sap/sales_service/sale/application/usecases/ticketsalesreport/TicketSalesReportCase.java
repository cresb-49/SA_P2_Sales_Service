package com.sap.sales_service.sale.application.usecases.ticketsalesreport;

import com.sap.sales_service.sale.application.factory.TicketSalesReportFactory;
import com.sap.sales_service.sale.application.input.TicketSalesReportCasePort;
import com.sap.sales_service.sale.application.ouput.TicketSalesReportPort;
import com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto.TicketSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TicketSalesReportCase implements TicketSalesReportCasePort {

    private final TicketSalesReportPort ticketSalesReportPort;
    private final TicketSalesReportFactory ticketSalesReportFactory;

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
}
