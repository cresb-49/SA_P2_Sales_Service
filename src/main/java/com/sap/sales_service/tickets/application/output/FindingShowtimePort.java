package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.dtos.ShowtimeView;

import java.util.UUID;

public interface FindingShowtimePort {
    ShowtimeView findShowtimeById(UUID showtimeId);
}
