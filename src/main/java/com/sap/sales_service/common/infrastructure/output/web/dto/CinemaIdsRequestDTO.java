package com.sap.sales_service.common.infrastructure.output.web.dto;

import java.util.List;
import java.util.UUID;

public record CinemaIdsRequestDTO(
        List<UUID> ids
) {
}