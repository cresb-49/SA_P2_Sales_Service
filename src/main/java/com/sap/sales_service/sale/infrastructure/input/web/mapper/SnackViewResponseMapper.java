package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.SnackResponseDTO;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SnackViewResponseMapper {

    public SnackResponseDTO toResponseDTO(SnackView domain) {
        if (domain == null) {
            return null;
        }
        return new SnackResponseDTO(
                domain.id(),
                domain.cinemaId(),
                domain.name(),
                domain.price(),
                domain.imageUrl(),
                null,
                null,
                null
        );
    }
}
