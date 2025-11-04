package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.sales_service.sale.application.usecases.topcinemasales.dto.TopCinemaSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.CinemaSalesSummaryResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.TopCinemaSalesReportResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TopCinemaSalesReportResponseMapper {

    public TopCinemaSalesReportResponseDTO toResponseDTO(TopCinemaSalesReportDTO dto) {
        return new TopCinemaSalesReportResponseDTO(
                dto.from(),
                dto.to(),
                dto.cinemas().stream()
                        .map(this::toSummaryResponse)
                        .toList()
        );
    }

    private CinemaSalesSummaryResponseDTO toSummaryResponse(CinemaSalesSummaryDTO dto) {
        String cinemaName = dto.cinema() != null ? dto.cinema().name() : null;
        return new CinemaSalesSummaryResponseDTO(
                dto.cinemaId(),
                cinemaName,
                dto.totalAmount(),
                dto.totalSales()
        );
    }
}
