package com.sap.sales_service.tickets.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import com.sap.sales_service.tickets.domain.dtos.ShowtimeView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TicketShowtimeViewMapper {

    public ShowtimeView toDomain(ShowtimeResponseDTO showtimeResponseDTO) {
        if (showtimeResponseDTO == null) {
            return null;
        }
        return new ShowtimeView(
                showtimeResponseDTO.startTime(),
                showtimeResponseDTO.endTime(),
                showtimeResponseDTO.ticketsAvailable()
        );
    }

    public List<ShowtimeView> toDomainList(List<ShowtimeResponseDTO> showtimeResponseDTOs) {
        return showtimeResponseDTOs.stream()
                .map(this::toDomain)
                .toList();
    }
}
