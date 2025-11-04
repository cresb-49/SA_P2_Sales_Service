package com.sap.sales_service.tickets.domain.dtos;

import java.time.LocalDateTime;

public record ShowtimeView(
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer maxCapacity
) {
}
