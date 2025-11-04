package com.sap.sales_service.sale.application.usecases.ticketsalesreport.dto;

import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;

import java.time.LocalDate;
import java.util.List;

public record TicketSalesReportDTO(
        List<TicketSalesByFunctionDTO> functions,
        Long totalTickets,
        LocalDate from,
        LocalDate to
) {
}
