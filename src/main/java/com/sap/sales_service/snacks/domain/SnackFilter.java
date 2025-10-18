package com.sap.sales_service.snacks.domain;

import java.util.UUID;

public record SnackFilter(
        String name,
        Boolean active,
        UUID cinemaId
) {
}
