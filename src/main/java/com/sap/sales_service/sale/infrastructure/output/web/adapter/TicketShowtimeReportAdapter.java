package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.sale.application.ouput.TicketShowtimeReportPort;
import com.sap.sales_service.sale.domain.dtos.reports.views.TicketShowtimeReportView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.TicketShowtimeReportMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketShowtimeReportAdapter implements TicketShowtimeReportPort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final TicketShowtimeReportMapper ticketShowtimeReportMapper;

    @Override
    public List<TicketShowtimeReportView> findShowtimesByIds(List<UUID> functionIds) {
        if (functionIds == null || functionIds.isEmpty()) {
            return List.of();
        }
        var dtos = cinemaGatewayPort.findFunctionsByIds(functionIds);
        return ticketShowtimeReportMapper.toViewList(dtos);
    }
}
