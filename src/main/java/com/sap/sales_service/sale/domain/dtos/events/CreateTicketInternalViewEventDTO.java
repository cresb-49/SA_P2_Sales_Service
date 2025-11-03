package com.sap.sales_service.sale.domain.dtos.events;

import java.util.UUID;

public record CreateTicketInternalViewEventDTO(
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID movieId
) {
}
