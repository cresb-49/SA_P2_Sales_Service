package com.sap.sales_service.tickets.infrastructure.output.jpa.mapper;

import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.infrastructure.output.jpa.entity.TicketEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TicketMapper {

    public Ticket toDomain(TicketEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Ticket(
                entity.getId(),
                entity.getSaleLineTicketId(),
                entity.getCinemaFunctionId(),
                entity.getCinemaId(),
                entity.getCinemaRoomId(),
                entity.getMovieId(),
                entity.isUsed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TicketEntity toEntity(Ticket domain) {
        if (domain == null) {
            return null;
        }
        return new TicketEntity(
                domain.getId(),
                domain.getSaleLineTicketId(),
                domain.getCinemaFunctionId(),
                domain.getCinemaId(),
                domain.getCinemaRoomId(),
                domain.getMovieId(),
                domain.isUsed(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

}
