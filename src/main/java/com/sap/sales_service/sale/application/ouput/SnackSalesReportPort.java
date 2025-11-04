package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SnackSalesReportPort {
    List<SnackSalesSummaryDTO> getSnackSalesSummary(LocalDateTime from, LocalDateTime to, UUID cinemaId);
}
