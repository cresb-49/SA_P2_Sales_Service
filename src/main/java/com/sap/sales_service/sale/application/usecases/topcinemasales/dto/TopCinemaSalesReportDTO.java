package com.sap.sales_service.sale.application.usecases.topcinemasales.dto;

import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public record TopCinemaSalesReportDTO(
        List<CinemaSalesSummaryDTO> cinemas,
        LocalDate from,
        LocalDate to
) {
}
