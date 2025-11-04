package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.SnackSalesSummaryView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SnackSalesSummaryMapper {
    public SnackSalesSummaryDTO toDomain(SnackSalesSummaryView view) {
        return new SnackSalesSummaryDTO(
                view.getSnackId(),
                view.getTotalQuantity(),
                null
        );
    }
}
