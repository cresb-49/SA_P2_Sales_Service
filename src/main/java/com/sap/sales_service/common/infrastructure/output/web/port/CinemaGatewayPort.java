package com.sap.sales_service.common.infrastructure.output.web.port;

import com.sap.sales_service.common.infrastructure.output.web.dto.domain.FunctionWebViewDTO;
import com.sap.sales_service.common.infrastructure.output.web.dto.service.ShowtimeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CinemaGatewayPort {
    boolean existsById(UUID cinemaId);
    ShowtimeResponseDTO findFunctionById(UUID functionId);
    List<ShowtimeResponseDTO> findFunctionsByIds(List<UUID> functionIds);
}
