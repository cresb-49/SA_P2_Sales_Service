package com.sap.sales_service.common.infrastructure.output.web.gateway;

import com.sap.sales_service.common.infrastructure.output.web.dto.FunctionWebViewDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CinemaGateway implements CinemaGatewayPort {

    private final UUID movieId = UUID.fromString("39ac6172-735b-4097-8f88-f307a83c4109");
    private final UUID cinemaId = UUID.fromString("9f8ce564-3846-49ed-bead-d73f4e1541be");

    @Override
    public boolean existsById(UUID cinemaId) {
        return true;
    }

    @Override
    public FunctionWebViewDTO findFunctionById(UUID functionId) {
        return new FunctionWebViewDTO(
                functionId,
                movieId,
                cinemaId,
                UUID.fromString("d4f6e2e1-B56e-4c5e-9f6d-2f3e5b6c7d8e"),
                BigDecimal.valueOf(75.00),
                LocalDateTime.of(2024, 7, 1, 18, 0),
                LocalDateTime.of(2024, 7, 1, 20, 0),
                5
        );
    }

    @Override
    public List<FunctionWebViewDTO> findFunctionsByIds(List<UUID> functionIds) {
        return functionIds.stream().map(id -> new FunctionWebViewDTO(
                id,
                movieId,
                cinemaId,
                UUID.fromString("d4f6e2e1-B56e-4c5e-9f6d-2f3e5b6c7d8e"),
                BigDecimal.valueOf(75.00),
                LocalDateTime.of(2024, 7, 1, 18, 0),
                LocalDateTime.of(2024, 7, 1, 20, 0)
                ,5
        )).toList();
    }
}
