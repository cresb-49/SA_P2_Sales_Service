package com.sap.sales_service.tickets.application.input;

import java.util.UUID;

public interface GetOccupiedSetsByCinemaFunctionPort {
    Integer getOccupiedSeatsByCinemaFunctionId(UUID cinemaFunctionId);
}
