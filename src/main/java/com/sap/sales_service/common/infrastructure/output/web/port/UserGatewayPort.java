package com.sap.sales_service.common.infrastructure.output.web.port;

import java.util.UUID;

public interface UserGatewayPort {
    boolean existsById(UUID userId);
}
