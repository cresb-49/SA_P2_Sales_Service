package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.SnackView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSnackPort {
    Optional<SnackView> findById(UUID id);

    List<SnackView> findAllById(List<UUID> ids);
}
