package com.sap.sales_service.sale.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CinemaViewMapper {

    public CinemaView toDomain(CinemaResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new CinemaView(
                dto.id(),
                dto.name()
        );
    }

    public List<CinemaView> toDomainList(List<CinemaResponseDTO> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }
}
