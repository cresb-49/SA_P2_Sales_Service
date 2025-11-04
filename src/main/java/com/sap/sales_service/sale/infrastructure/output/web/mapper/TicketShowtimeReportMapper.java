package com.sap.sales_service.sale.infrastructure.output.web.mapper;

import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaHallResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.CinemaResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TicketShowtimeReportMapper {

    public TicketShowtimeReportView toView(ShowtimeResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        CinemaHallResponseDTO hall = dto.hall();
        CinemaResponseDTO cinema = hall != null ? hall.cinema() : null;

        CinemaView cinemaView = null;
        if (cinema != null) {
            cinemaView = new CinemaView(
                    cinema.id(),
                    cinema.name()
            );
        }

        return new TicketShowtimeReportView(
                dto.id(),
                hall != null ? hall.id() : null,
                hall != null ? hall.name() : null,
                cinemaView,
                dto.startTime(),
                dto.endTime(),
                dto.ticketsAvailable()
        );
    }

    public List<TicketShowtimeReportView> toViewList(List<ShowtimeResponseDTO> dtos) {
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(this::toView)
                .toList();
    }
}
