package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.TicketResponseDTO;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TicketViewResponseMapper {

    public TicketResponseDTO toResponseDTO(TicketView domain) {
        if (domain == null) {
            return null;
        }
        return new TicketResponseDTO(
                domain.id(),
                domain.saleLineTicketId(),
                domain.cinemaFunctionId(),
                domain.cinemaId(),
                domain.cinemaRoomId(),
                domain.seatId(),
                domain.movieId(),
                domain.used(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}
