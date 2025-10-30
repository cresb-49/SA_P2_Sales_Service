package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SnackReportByCinemaFactory {
    private final FindSnackPort findSnackPort;

    public SnackSalesByCinemaDTO withSnackView(SnackSalesByCinemaDTO dto) {
        var snackView = findSnackPort.findById(dto.snackId()).orElse(null);
        return new SnackSalesByCinemaDTO(
                dto.cinemaId(),
                dto.snackId(),
                dto.totalQuantity(),
                dto.totalAmount(),
                snackView
        );
    }

    public List<SnackSalesByCinemaDTO> withSnackView(List<SnackSalesByCinemaDTO> dtos) {
        var snacksUUIDs = dtos.stream()
                .map(SnackSalesByCinemaDTO::snackId)
                .toList();
        var snacks = findSnackPort.findAllById(snacksUUIDs);
        // Map snacks to with their IDs for easy access
        var snacksMap = snacks.stream()
                .collect(java.util.stream.Collectors.toMap(SnackView::id, snack -> snack));
        // Set snacks to dtos
        return dtos.stream().map(dto -> {
            var snackView = snacksMap.get(dto.snackId());
            return new SnackSalesByCinemaDTO(
                    dto.cinemaId(),
                    dto.snackId(),
                    dto.totalQuantity(),
                    dto.totalAmount(),
                    snackView
            );
        }).toList();
    }
}
