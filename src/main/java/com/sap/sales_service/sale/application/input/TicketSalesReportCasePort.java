package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto.TicketSalesReportDTO;

import java.time.LocalDate;

public interface TicketSalesReportCasePort {
    TicketSalesReportDTO report(LocalDate from, LocalDate to);
}
