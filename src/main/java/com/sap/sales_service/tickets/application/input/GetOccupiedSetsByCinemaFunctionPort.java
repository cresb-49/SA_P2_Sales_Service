package com.sap.sales_service.tickets.application.input;

import java.util.List;
import java.util.UUID;

public interface GetOccupiedSetsByCinemaFunctionPort {
    List<UUID> getOccupiedSeatsByCinemaFunctionId(UUID cinemaFunctionId);
}
