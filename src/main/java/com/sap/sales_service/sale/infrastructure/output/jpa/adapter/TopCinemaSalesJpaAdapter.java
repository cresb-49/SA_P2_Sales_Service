package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.TopCinemaSalesReportPort;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.CinemaSalesSummaryMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class TopCinemaSalesJpaAdapter implements TopCinemaSalesReportPort {

    private final SaleEntityRepository saleEntityRepository;
    private final CinemaSalesSummaryMapper cinemaSalesSummaryMapper;

    @Override
    public List<CinemaSalesSummaryDTO> getTopCinemaSales(LocalDateTime from, LocalDateTime to, int limit) {
        return saleEntityRepository.findTopCinemaSales(from, to, limit).stream()
                .map(cinemaSalesSummaryMapper::toDomain)
                .toList();
    }
}
