package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.dtos.reports.TicketSalesByFunctionDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.TicketSalesByFunctionView;
import org.springframework.stereotype.Component;

@Component
public class TicketSalesByFunctionMapper {

    public TicketSalesByFunctionDTO toDomain(TicketSalesByFunctionView view) {
        return new TicketSalesByFunctionDTO(
                view.getFunctionId(),
                view.getCinemaId(),
                view.getCinemaRoomId(),
                view.getMovieId(),
                view.getTicketsSold(),
                null,
                null
        );
    }
}
