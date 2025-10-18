package com.sap.sales_service.tickets.application.usecases.createticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.tickets.application.input.CreateTicketPort;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.application.output.ResponseSaleLineTicketPort;
import com.sap.sales_service.tickets.application.output.SaveTicketPort;
import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import com.sap.sales_service.tickets.domain.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateTicketCase implements CreateTicketPort {

    private final FindingTicketPort findingTicketPort;
    private final SaveTicketPort saveTicketPort;
    private final ResponseSaleLineTicketPort responseSaleLineTicketPort;

    @Override
    public Ticket createTicket(CreateTicketDTO createTicketDTO) {
        // Find by SaleLineTicketId to avoid duplicates
        Ticket existingTicket = findingTicketPort.findBySaleLineTicketId(createTicketDTO.saleLineTicketId()).orElse(null);
        if (existingTicket != null) {
            throw new NonRetryableBusinessException("Ticket with SaleLineTicketId " + createTicketDTO.saleLineTicketId() + " already exists");
        }
        // Verify if the seat is already taken for the given cinema function
        findingTicketPort.findByCinemaFunctionIdAndSeatId(
                createTicketDTO.cinemaFunctionId(),
                createTicketDTO.seatId()
        ).ifPresent(ticket -> {
            responseSaleLineTicketPort.respondSaleLineTicket(createTicketDTO.saleLineTicketId(), TicketStatusType.IN_USE, "Seat is already taken");
            throw new NonRetryableBusinessException("Seat with id " + createTicketDTO.seatId() + " is already taken for cinema function id " + createTicketDTO.cinemaFunctionId());
        });
        // Create and save new ticket
        Ticket newTicket = new Ticket(
                createTicketDTO.saleLineTicketId(),
                createTicketDTO.cinemaFunctionId(),
                createTicketDTO.cinemaId(),
                createTicketDTO.cinemaRoomId(),
                createTicketDTO.seatId(),
                createTicketDTO.movieId()
        );
        saveTicketPort.save(newTicket);
        // Send response back to Sale Service
        responseSaleLineTicketPort.respondSaleLineTicket(newTicket.getSaleLineTicketId(), TicketStatusType.RESERVED, "Ticket created successfully");
        return newTicket;
    }
}
