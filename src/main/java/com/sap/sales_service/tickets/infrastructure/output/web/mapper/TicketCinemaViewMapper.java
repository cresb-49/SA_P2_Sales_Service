package com.sap.sales_service.tickets.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.tickets.domain.dtos.CinemaView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TicketCinemaViewMapper {

    public CinemaView toDomain(CinemaResponseDTO cinemaResponseDTO) {
        if (cinemaResponseDTO == null) {
            return null;
        }
        return new CinemaView(cinemaResponseDTO.name());
    }

    public List<CinemaView> toDomainList(List<CinemaResponseDTO> cinemaResponseDTOs) {
        return cinemaResponseDTOs.stream()
                .map(this::toDomain)
                .toList();
    }
}
