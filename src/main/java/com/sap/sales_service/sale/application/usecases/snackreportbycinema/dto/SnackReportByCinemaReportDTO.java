package com.sap.sales_service.sale.application.usecases.snackreportbycinema.dto;

import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SnackReportByCinemaReportDTO(
        List<SnackSalesByCinemaDTO> snackSalesByCinemaDTOs,
        BigDecimal totalAmount,
        CinemaView cinema,
        LocalDate from,
        LocalDate to
) {
}
