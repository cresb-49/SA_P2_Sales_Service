package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.dtos.CinemaView;

import java.util.UUID;

public interface FindingCinemaPort {
    CinemaView findCinemaById(UUID cinemaId);
}
