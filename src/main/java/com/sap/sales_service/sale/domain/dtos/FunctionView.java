package com.sap.sales_service.sale.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FunctionView(
        UUID id,
        UUID movieId,
        UUID cinemaId,
        UUID cinemaRoomId,
        BigDecimal price,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxTicketsAvailable
) {

}
