package com.sap.sales_service.sale.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class FunctionViewMapper {

    public FunctionView toDomain(ShowtimeResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new FunctionView(
                dto.id(),
                dto.cinemaMovie().movieId(),
                dto.cinemaMovie().cinema().id(),
                dto.hall().id(),
                dto.price(),
                dto.startTime(),
                dto.endTime(),
                dto.ticketsAvailable()
        );
    }

    public List<FunctionView> toDomainList(List<ShowtimeResponseDTO> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }

}
