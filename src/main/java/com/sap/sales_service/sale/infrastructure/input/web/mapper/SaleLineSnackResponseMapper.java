package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.SaleLineSnackResponseDTO;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleLineSnackResponseMapper {

    private final SnackViewResponseMapper snackViewResponseMapper;

    public SaleLineSnackResponseDTO toResponseDTO(SaleLineSnack domain) {
        if (domain == null) {
            return null;
        }
        return new SaleLineSnackResponseDTO(
                domain.getId(),
                domain.getSaleId(),
                domain.getSnackId(),
                domain.getQuantity(),
                domain.getUnitPrice(),
                domain.getTotalPrice(),
                snackViewResponseMapper.toResponseDTO(domain.getSnackView())
        );
    }
}
