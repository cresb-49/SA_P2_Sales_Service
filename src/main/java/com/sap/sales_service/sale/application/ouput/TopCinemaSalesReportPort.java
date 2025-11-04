package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TopCinemaSalesReportPort {
    List<CinemaSalesSummaryDTO> getTopCinemaSales(LocalDateTime from, LocalDateTime to, int limit);
}
