package com.sap.sales_service.sale.application.usecases.find.dtos;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.sales_service.sale.domain.filter.SaleFilter;

import java.time.LocalDateTime;
import java.util.UUID;

public record SaleFilterDTO(
        UUID clientId,
        SaleStatusType status,
        UUID cinemaId,
        LocalDateTime maxCreatedAt,
        LocalDateTime minCreatedAt,
        LocalDateTime maxUpdatedAt,
        LocalDateTime minUpdatedAt,
        LocalDateTime maxPaidAt,
        LocalDateTime minPaidAt
) {
    public SaleFilter toDomain() {
        return SaleFilter.builder()
                .clientId(this.clientId)
                .status(this.status)
                .cinemaId(this.cinemaId)
                .maxCreatedAt(this.maxCreatedAt)
                .minCreatedAt(this.minCreatedAt)
                .maxUpdatedAt(this.maxUpdatedAt)
                .minUpdatedAt(this.minUpdatedAt)
                .maxPaidAt(this.maxPaidAt)
                .minPaidAt(this.minPaidAt)
                .build();
    }

    public SaleFilter withCinemaId(UUID cinemaId) {
        return SaleFilter.builder()
                .clientId(this.clientId)
                .status(this.status)
                .cinemaId(cinemaId)
                .maxCreatedAt(this.maxCreatedAt)
                .minCreatedAt(this.minCreatedAt)
                .maxUpdatedAt(this.maxUpdatedAt)
                .minUpdatedAt(this.minUpdatedAt)
                .maxPaidAt(this.maxPaidAt)
                .minPaidAt(this.minPaidAt)
                .build();
    }

    public SaleFilter withClientId(UUID clientId) {
        return SaleFilter.builder()
                .clientId(clientId)
                .status(this.status)
                .cinemaId(this.cinemaId)
                .maxCreatedAt(this.maxCreatedAt)
                .minCreatedAt(this.minCreatedAt)
                .maxUpdatedAt(this.maxUpdatedAt)
                .minUpdatedAt(this.minUpdatedAt)
                .maxPaidAt(this.maxPaidAt)
                .minPaidAt(this.minPaidAt)
                .build();
    }
}
