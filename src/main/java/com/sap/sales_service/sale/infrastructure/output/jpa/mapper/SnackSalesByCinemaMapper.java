package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.SnackSalesByCinemaView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SnackSalesByCinemaMapper {
    public SnackSalesByCinemaDTO toDomain(SnackSalesByCinemaView view) {
        return new SnackSalesByCinemaDTO(
                view.getCinemaId(),
                view.getSnackId(),
                view.getTotalQuantity(),
                view.getTotalAmount(),
                null // Optional snack view can be set to null or mapped if available
        );
    }
}
