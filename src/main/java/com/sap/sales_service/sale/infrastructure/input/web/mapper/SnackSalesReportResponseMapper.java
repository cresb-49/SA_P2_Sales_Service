package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.sales_service.sale.application.usecases.snacksalesreport.dto.SnackSalesReportDTO;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.SnackSalesReportResponseDTO;
import com.sap.sales_service.sale.infrastructure.input.web.dto.SnackSalesSummaryResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SnackSalesReportResponseMapper {

    public SnackSalesReportResponseDTO toResponseDTO(SnackSalesReportDTO dto) {
        return new SnackSalesReportResponseDTO(
                dto.from(),
                dto.to(),
                dto.totalQuantity(),
                dto.cinema(),
                dto.cinemaId(),
                dto.snacks().stream()
                        .map(this::toSummaryResponseDTO)
                        .toList()
        );
    }

    private SnackSalesSummaryResponseDTO toSummaryResponseDTO(SnackSalesSummaryDTO dto) {
        String snackName = dto.snack() != null ? dto.snack().name() : null;
        return new SnackSalesSummaryResponseDTO(
                dto.snackId(),
                snackName,
                dto.totalQuantity()
        );
    }
}
