package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackSalesReportFactory {
    private final FindSnackPort findSnackPort;

    public SnackSalesSummaryDTO withSnackView(SnackSalesSummaryDTO dto) {
        var snackView = findSnackPort.findById(dto.snackId()).orElse(null);
        return new SnackSalesSummaryDTO(
                dto.snackId(),
                dto.totalQuantity(),
                snackView
        );
    }

    public List<SnackSalesSummaryDTO> withSnackView(List<SnackSalesSummaryDTO> dtos) {
        if (dtos.isEmpty()) {
            return dtos;
        }

        var snackIds = dtos.stream()
                .map(SnackSalesSummaryDTO::snackId)
                .toList();
        var snacks = findSnackPort.findAllById(snackIds);
        var snacksMap = snacks.stream()
                .collect(java.util.stream.Collectors.toMap(SnackView::id, snack -> snack));

        return dtos.stream()
                .map(dto -> new SnackSalesSummaryDTO(
                        dto.snackId(),
                        dto.totalQuantity(),
                        snacksMap.get(dto.snackId())
                ))
                .toList();
    }
}
