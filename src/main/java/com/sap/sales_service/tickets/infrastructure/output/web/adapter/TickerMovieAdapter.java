package com.sap.sales_service.tickets.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.MovieGatewayPort;
import com.sap.sales_service.tickets.application.output.FindingMoviePort;
import com.sap.sales_service.tickets.domain.dtos.MovieView;
import com.sap.sales_service.tickets.infrastructure.output.web.mapper.TicketMovieViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class TickerMovieAdapter implements FindingMoviePort {

    private final MovieGatewayPort movieGatewayPort;
    private final TicketMovieViewMapper ticketMovieViewMapper;

    @Override
    public MovieView findMovieById(UUID movieId) {
        var movieDto = movieGatewayPort.getMovieById(movieId);
        return ticketMovieViewMapper.toDomain(movieDto);
    }
}
