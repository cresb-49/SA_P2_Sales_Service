package com.sap.sales_service.sale.infrastructure.output.domain.mapper;

import com.sap.sales_service.sale.domain.dtos.TicketView;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SaleTicketViewMapper {
    public TicketView toView(TicketDomainView domainView) {
        if (domainView == null) {
            return null;
        }
        return new TicketView(
                domainView.id(),
                domainView.saleLineTicketId(),
                domainView.cinemaFunctionId(),
                domainView.cinemaId(),
                domainView.cinemaRoomId(),
                domainView.seatId(),
                domainView.movieId(),
                domainView.used(),
                domainView.createdAt(),
                domainView.updatedAt()
        );
    }

    public List<TicketView> toViewList(List<TicketDomainView> domainViews) {
        if (domainViews == null) {
            return null;
        }
        return domainViews.stream()
                .map(this::toView)
                .toList();
    }
}
