package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.CinemaViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleCinemaAdapter implements FindCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final CinemaViewMapper cinemaViewMapper;

    @Override
    public boolean existsById(UUID cinemaId) {
        return cinemaGatewayPort.existsById(cinemaId);
    }

    @Override
    public CinemaView findById(UUID id) {
        var cinemaDto = cinemaGatewayPort.findCinemaById(id);
        return cinemaViewMapper.toDomain(cinemaDto);
    }
}
