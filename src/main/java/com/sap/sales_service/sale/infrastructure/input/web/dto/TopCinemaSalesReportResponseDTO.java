package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.time.LocalDate;
import java.util.List;

public record TopCinemaSalesReportResponseDTO(
        LocalDate from,
        LocalDate to,
        List<CinemaSalesSummaryResponseDTO> cinemas
) {
}
