package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.CinemaSalesSummaryView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CinemaSalesSummaryMapper {

    public CinemaSalesSummaryDTO toDomain(CinemaSalesSummaryView view) {
        return new CinemaSalesSummaryDTO(
                view.getCinemaId(),
                view.getTotalAmount(),
                view.getTotalSales(),
                null
        );
    }
}
