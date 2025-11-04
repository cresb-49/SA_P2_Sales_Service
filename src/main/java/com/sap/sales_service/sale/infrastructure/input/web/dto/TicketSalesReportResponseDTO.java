package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.time.LocalDate;
import java.util.List;

public record TicketSalesReportResponseDTO(
        LocalDate from,
        LocalDate to,
        Long totalTickets,
        List<TicketSalesByFunctionResponseDTO> functions
) {
}
