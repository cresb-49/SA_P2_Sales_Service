package com.sap.sales_service.common.infrastructure.output.web.port;

import com.sap.sales_service.common.infrastructure.output.web.dto.domain.FunctionWebViewDTO;

import java.util.List;
import java.util.UUID;

public interface CinemaGatewayPort {
    boolean existsById(UUID cinemaId);
    FunctionWebViewDTO findFunctionById(UUID functionId);
    List<FunctionWebViewDTO> findFunctionsByIds(List<UUID> functionIds);
}
