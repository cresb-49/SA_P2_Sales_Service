package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;

import java.util.List;
import java.util.UUID;

public interface TicketShowtimeReportPort {
    List<TicketShowtimeReportView> findShowtimesByIds(List<UUID> functionIds);
}
