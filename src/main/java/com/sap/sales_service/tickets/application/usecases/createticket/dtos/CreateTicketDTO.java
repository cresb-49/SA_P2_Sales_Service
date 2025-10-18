package com.sap.sales_service.tickets.application.usecases.createticket.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTicketDTO(
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID seatId,
        UUID movieId
) {
}
