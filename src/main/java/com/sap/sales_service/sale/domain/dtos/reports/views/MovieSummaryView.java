package com.sap.sales_service.sale.domain.dtos.reports.views;

import java.util.UUID;

public record MovieSummaryView(
        UUID id,
        String title
) {
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
