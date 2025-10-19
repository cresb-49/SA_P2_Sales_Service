package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.FunctionView;

import java.util.List;
import java.util.UUID;

public interface FindFunctionPort {
    FunctionView findById(UUID id);
    List<FunctionView> findByFunctionIds(List<UUID> functionIds);
}
