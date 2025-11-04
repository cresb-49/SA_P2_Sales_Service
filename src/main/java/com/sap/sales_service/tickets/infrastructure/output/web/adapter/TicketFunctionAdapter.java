package com.sap.sales_service.tickets.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.tickets.application.output.FindingShowtimePort;
import com.sap.sales_service.tickets.domain.dtos.ShowtimeView;
import com.sap.sales_service.tickets.infrastructure.output.web.mapper.TicketShowtimeViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketFunctionAdapter implements FindingShowtimePort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final TicketShowtimeViewMapper ticketShowtimeViewMapper;

    @Override
    public ShowtimeView findShowtimeById(UUID showtimeId) {
        var showtimeDto = cinemaGatewayPort.findFunctionById(showtimeId);
        return ticketShowtimeViewMapper.toDomain(showtimeDto);
    }
}
