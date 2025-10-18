package com.sap.sales_service.tickets.infrastructure.input.domain.mapper;

import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TicketDomainViewMapper {

    public TicketDomainView toDomainView(Ticket domain) {
        if (domain == null) {
            return null;
        }
        return new TicketDomainView(
                domain.getId(),
                domain.getSaleLineTicketId(),
                domain.getCinemaFunctionId(),
                domain.getCinemaId(),
                domain.getCinemaRoomId(),
                domain.getSeatId(),
                domain.getMovieId(),
                domain.isUsed(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
