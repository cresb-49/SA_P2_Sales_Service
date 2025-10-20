package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.sale.application.ouput.FindFunctionPort;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.FunctionViewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SaleFunctionAdapter implements FindFunctionPort {

    private final CinemaGatewayPort cinemaGatewayPort;
    private final FunctionViewMapper functionViewMapper;

    @Override
    public FunctionView findById(UUID id) {
        return functionViewMapper.toDomain(
                cinemaGatewayPort.findFunctionById(id));
    }

    @Override
    public List<FunctionView> findByFunctionIds(List<UUID> functionIds) {
        return functionViewMapper.toDomainList(
                cinemaGatewayPort.findFunctionsByIds(functionIds)
        );
    }
}
