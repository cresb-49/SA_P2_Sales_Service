package com.sap.sales_service.sale.domain.dtos;

import java.util.UUID;

public record CinemaView(
        UUID id,
        String name
) {
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
