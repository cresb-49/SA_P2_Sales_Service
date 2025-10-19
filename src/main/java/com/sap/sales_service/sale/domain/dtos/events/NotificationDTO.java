package com.sap.sales_service.sale.domain.dtos.events;

import java.util.UUID;

public record NotificationDTO(
        UUID userId,
        String mensaje
) {
}
