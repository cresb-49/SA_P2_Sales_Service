package com.sap.sales_service.sale.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketView(
        UUID id,
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID movieId,
        Boolean used,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}