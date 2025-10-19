package com.sap.sales_service.sale.domain.dtos.events;

import java.util.UUID;

public record CreateTicketEventDTO(
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID seatId,
        UUID movieId
) {
}
