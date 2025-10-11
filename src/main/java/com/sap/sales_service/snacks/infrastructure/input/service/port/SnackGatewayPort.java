package com.sap.sales_service.snacks.infrastructure.input.service.port;

import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnackGatewayPort {
    Optional<SnackInternalView> findById(UUID id);

    List<SnackInternalView> findByIds(List<String> ids);
}
