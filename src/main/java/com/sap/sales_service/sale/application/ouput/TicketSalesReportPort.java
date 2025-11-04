package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketSalesReportPort {
    List<TicketSalesByFunctionDTO> getTicketSales(LocalDateTime from, LocalDateTime to);
}
