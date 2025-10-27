package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SnackReportByCinema {
    List<SnackSalesByCinemaDTO> getSnackSalesByCinemaDTOs(LocalDateTime from, LocalDateTime to, UUID cinemaId);
}
