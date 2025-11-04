package com.sap.sales_service.tickets.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.tickets.application.output.FindingCinemaPort;
import com.sap.sales_service.tickets.domain.dtos.CinemaView;
import com.sap.sales_service.tickets.infrastructure.output.web.mapper.TicketCinemaViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketCinemaAdapter implements FindingCinemaPort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final TicketCinemaViewMapper ticketCinemaViewMapper;

    @Override
    public CinemaView findCinemaById(UUID cinemaId) {
        var cinemaDto = cinemaGatewayPort.findCinemaById(cinemaId);
        return ticketCinemaViewMapper.toDomain(cinemaDto);
    }
}
