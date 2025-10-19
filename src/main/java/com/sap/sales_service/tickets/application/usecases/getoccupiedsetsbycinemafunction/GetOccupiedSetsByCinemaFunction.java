package com.sap.sales_service.tickets.application.usecases.getoccupiedsetsbycinemafunction;

import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class GetOccupiedSetsByCinemaFunction implements GetOccupiedSetsByCinemaFunctionPort {

    private final FindingByFilterPort findingByFilterPort;

    @Override
    public List<UUID> getOccupiedSeatsByCinemaFunctionId(UUID cinemaFunctionId) {
        if (cinemaFunctionId == null) {
            return List.of();
        }
        var filter = TicketFilter.builder().cinemaFunctionId(cinemaFunctionId).build();
        var entries = findingByFilterPort.findByFilter(filter);
        return entries.stream()
                .map(Ticket::getSeatId)
                .toList();
    }
}
