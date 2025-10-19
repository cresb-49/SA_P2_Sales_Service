package com.sap.sales_service.sale.infrastructure.output.domain.adapter;

import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.infrastructure.output.domain.mapper.SaleSnackViewMapper;
import com.sap.sales_service.sale.infrastructure.output.domain.mapper.SaleTicketViewMapper;

import com.sap.sales_service.snacks.infrastructure.input.service.port.SnackGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleSnackAdapter implements FindSnackPort {

    private final SnackGatewayPort snackGatewayPort;
    private final SaleSnackViewMapper snackViewMapper;


    @Override
    public Optional<SnackView> findById(UUID id) {
        return snackGatewayPort.findById(id)
                .map(snackViewMapper::toView);
    }

    @Override
    public List<SnackView> findAllById(List<UUID> ids) {
        return snackViewMapper.toViewList(
                snackGatewayPort.findByIds(ids)
        );
    }
}
