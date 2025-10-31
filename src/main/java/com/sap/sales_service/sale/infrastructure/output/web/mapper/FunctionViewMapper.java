package com.sap.sales_service.sale.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.FunctionWebViewDTO;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class FunctionViewMapper {

    public FunctionView toDomain(FunctionWebViewDTO dto) {
        if (dto == null) {
            return null;
        }
        return new FunctionView(
                dto.id(),
                dto.movieId(),
                dto.cinemaId(),
                dto.cinemaRoomId(),
                dto.price(),
                dto.startTime(),
                dto.endTime(),
                dto.ticketsAvailable()
        );
    }

    public List<FunctionView> toDomainList(List<FunctionWebViewDTO> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }

}
