package com.sap.sales_service.tickets.application.usecases.getoccupiedsetsbycinemafunction;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
        var filter = TicketFilter.builder()
                .cinemaFunctionId(cinemaFunctionId)
                .ticketStatus(TicketStatusType.RESERVED)
                .build();
        var entries = findingByFilterPort.findByFilter(filter);
        var filter2 = TicketFilter.builder()
                .cinemaFunctionId(cinemaFunctionId)
                .ticketStatus(TicketStatusType.PURCHASED)
                .build();
        var entries2 = findingByFilterPort.findByFilter(filter2);
        List<Ticket> combined = Stream.concat(entries.stream(), entries2.stream()).toList();

        return combined.stream()
                .map(Ticket::getSeatId)
                .distinct()
                .toList();
    }
}
