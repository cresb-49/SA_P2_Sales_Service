package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.port.UserGatewayPort;
import com.sap.sales_service.sale.application.ouput.FindUserPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleUserAdapter implements FindUserPort {
    private final UserGatewayPort userGatewayPort;
    @Override
    public boolean existsById(UUID userId) {
        return userGatewayPort.existsById(userId);
    }
}
