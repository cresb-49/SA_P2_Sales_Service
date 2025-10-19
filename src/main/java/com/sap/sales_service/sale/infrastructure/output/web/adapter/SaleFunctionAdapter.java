package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.sale.application.ouput.FindFunctionPort;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SaleFunctionAdapter implements FindFunctionPort {

    private final UUID movieId = UUID.fromString("39ac6172-735b-4097-8f88-f307a83c4109");
    private final UUID cinemaId = UUID.fromString("f40137e0-6559-438e-97e1-85767c7ff0ae");

    @Override
    public FunctionView findById(UUID id) {
        return new FunctionView(
                id,
                movieId,
                cinemaId,
                UUID.fromString("d4f6e2e1-B56e-4c5e-9f6d-2f3e5b6c7d8e"),
                BigDecimal.valueOf(75.00),
                LocalDateTime.of(2024, 7, 1, 18, 0),
                LocalDateTime.of(2024, 7, 1, 20, 0)
        );
    }

    @Override
    public List<FunctionView> findByFunctionIds(List<UUID> functionIds) {
        return functionIds.stream().map(id -> new FunctionView(
                id,
                movieId,
                cinemaId,
                UUID.fromString("d4f6e2e1-B56e-4c5e-9f6d-2f3e5b6c7d8e"),
                BigDecimal.valueOf(75.00),
                LocalDateTime.of(2024, 7, 1, 18, 0),
                LocalDateTime.of(2024, 7, 1, 20, 0)
        )).toList();
    }
}
