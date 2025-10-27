package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.CinemaView;

import java.util.UUID;

public interface FindCinemaPort {
    boolean existsById(UUID cinemaId);

    CinemaView findById(UUID id);
}
