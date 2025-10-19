package com.sap.sales_service.sale.domain.filter;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@With
public record SaleFilter(
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
}
