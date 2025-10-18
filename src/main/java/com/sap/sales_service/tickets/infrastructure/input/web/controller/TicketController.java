package com.sap.sales_service.tickets.infrastructure.input.web.controller;

import com.sap.sales_service.tickets.application.input.FindTicketPort;
import com.sap.sales_service.tickets.application.input.MarkUsedTicketPort;
import com.sap.sales_service.tickets.infrastructure.input.web.mapper.TicketResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
public class TicketController {

    private final FindTicketPort findTicketPort;
    private final MarkUsedTicketPort markUsedTicketPort;
    private final TicketResponseMapper ticketResponseMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(UUID id) {
        var ticket = findTicketPort.findById(id);
        var responseDTO = ticketResponseMapper.toResponseDTO(ticket);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/mark-used/{id}")
    public ResponseEntity<?> markTicketAsUsed(UUID id) {
        markUsedTicketPort.markTicketAsUsed(id);
        return ResponseEntity.noContent().build();
    }
}
