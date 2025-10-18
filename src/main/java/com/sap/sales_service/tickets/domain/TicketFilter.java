package com.sap.sales_service.tickets.domain;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@With
public record TicketFilter(
        UUID userId,
        UUID saleLineTicketId,
        UUID cinemaFunctionId,
        UUID cinemaId,
        UUID cinemaRoomId,
        UUID seatId,
        UUID movieId,
        Boolean used,
        SaleStatusType saleStatus,
        TicketStatusType ticketStatus,
        LocalDateTime maxCreatedAt,
        LocalDateTime minCreatedAt,
        LocalDateTime maxUpdatedAt,
        LocalDateTime minUpdatedAt
) {

}
