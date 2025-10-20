package com.sap.sales_service.tickets.infrastructure.input.web.controller;

import com.sap.sales_service.tickets.application.input.FindTicketPort;
import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.input.MarkUsedTicketPort;
import com.sap.sales_service.tickets.infrastructure.input.web.mapper.TicketResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
public class TicketController {

    private final FindTicketPort findTicketPort;
    private final MarkUsedTicketPort markUsedTicketPort;
    private final TicketResponseMapper ticketResponseMapper;
    private final GetOccupiedSetsByCinemaFunctionPort getOccupiedSetsByCinemaFunctionPort;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable UUID id) {
        var ticket = findTicketPort.findById(id);
        var responseDTO = ticketResponseMapper.toResponseDTO(ticket);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/mark-used/{id}")
    public ResponseEntity<?> markTicketAsUsed(@PathVariable UUID id) {
        markUsedTicketPort.markTicketAsUsed(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/cinema-function/{cinemaFunctionId}/seats/occupied/ids")
    public ResponseEntity<List<UUID>> isSeatOccupied(@PathVariable UUID cinemaFunctionId) {
        var occupiedSeats = getOccupiedSetsByCinemaFunctionPort.getOccupiedSeatsByCinemaFunctionId(cinemaFunctionId);
        return ResponseEntity.ok(occupiedSeats);
    }
}
