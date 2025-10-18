package com.sap.sales_service.tickets.infrastructure.input.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketDomainView(
        UUID id,
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID seatId,
        UUID movieId,
        boolean used,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
