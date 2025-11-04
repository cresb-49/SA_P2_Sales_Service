package com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports;

import java.util.UUID;

public interface TicketSalesByFunctionView {
    UUID getFunctionId();
    UUID getCinemaId();
    UUID getCinemaRoomId();
    UUID getMovieId();
    Long getTicketsSold();
}
