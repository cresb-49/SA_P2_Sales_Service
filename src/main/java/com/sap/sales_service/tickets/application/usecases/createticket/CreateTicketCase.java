package com.sap.sales_service.tickets.application.usecases.createticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.tickets.application.input.CreateTicketPort;
import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.output.*;
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
    private final GetOccupiedSetsByCinemaFunctionPort getOccupiedSetsByCinemaFunctionPort;
    private final FindingShowtimePort findingShowtimePort;
    private final FindingMoviePort findingMoviePort;
    private final FindingCinemaPort findingCinemaPort;

    @Override
    public Ticket createTicket(CreateTicketDTO createTicketDTO) {
        // Find by SaleLineTicketId to avoid duplicates
        Ticket existingTicket = findingTicketPort.findBySaleLineTicketId(createTicketDTO.saleLineTicketId())
                .orElse(null);
        if (existingTicket != null) {
            throw new NonRetryableBusinessException(
                    "El boleto para la línea de venta con ID " + createTicketDTO.saleLineTicketId() + " ya existe.");
        }
        //find Movie
        var movie = findingMoviePort.findMovieById(createTicketDTO.movieId());
        var function = findingShowtimePort.findShowtimeById(createTicketDTO.cinemaFunctionId());
        var cinema = findingCinemaPort.findCinemaById(createTicketDTO.cinemaId());
        // Get occupied sets for the cinema function
        var occupiedSets = getOccupiedSetsByCinemaFunctionPort.getOccupiedSeatsByCinemaFunctionId(
                createTicketDTO.cinemaFunctionId());
        if (occupiedSets >= function.maxCapacity()) {
            // Send response back to Sale Service
            var message = "No hay asientos disponibles para la función de cine de la película " + movie.name() + " en la función de hora " + function.startTime() + ", del cine " + cinema.name() + ".";
            responseSaleLineTicketPort.respondSaleLineTicket(createTicketDTO.saleLineTicketId(), TicketStatusType.IN_USE, message);
            throw new NonRetryableBusinessException(message);
        }
        // Create and save new ticket
        Ticket newTicket = new Ticket(
                createTicketDTO.saleLineTicketId(),
                createTicketDTO.cinemaFunctionId(),
                createTicketDTO.cinemaId(),
                createTicketDTO.cinemaRoomId(),
                createTicketDTO.movieId());
        saveTicketPort.save(newTicket);
        // Send response back to Sale Service
        responseSaleLineTicketPort.respondSaleLineTicket(newTicket.getSaleLineTicketId(), TicketStatusType.RESERVED,
                "El boleto " + newTicket.getId() + " ha sido creado y reservado exitosamente para la película " + movie.name() + " en la función de hora " + function.startTime() + ", del cine " + cinema.name() + ".");
        return newTicket;
    }
}
