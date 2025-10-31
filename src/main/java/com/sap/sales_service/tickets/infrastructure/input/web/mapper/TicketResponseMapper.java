package com.sap.sales_service.tickets.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.TicketResponseDTO;
import com.sap.sales_service.tickets.domain.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TicketResponseMapper {

    public TicketResponseDTO toResponseDTO(Ticket domain) {
        if (domain == null) {
            return null;
        }
        return new TicketResponseDTO(
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

    public List<TicketResponseDTO> toResponseDTOList(List<Ticket> domainList) {
        return domainList.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Page<TicketResponseDTO> toResponseDTOPage(Page<Ticket> domainPage) {
        return domainPage.map(this::toResponseDTO);
    }
}
