package com.sap.sales_service.common.infrastructure.output.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FunctionWebViewDTO(
        UUID id,
        UUID movieId,
        UUID cinemaId,
        UUID cinemaRoomId,
        BigDecimal price,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

}
