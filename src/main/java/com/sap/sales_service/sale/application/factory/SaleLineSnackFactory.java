package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSaleLineSnackPort;
import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SaleLineSnackFactory {
    private final FindSaleLineSnackPort findSaleLineSnackPort;
    private final FindSnackPort findSnackPort;

    public List<SaleLineSnack> saleLineSnackWithAllRelations(UUID saleId) {
        var saleLinesSnacks = findSaleLineSnackPort.findAllBySaleId(saleId);
        var snacksUUIDs = saleLinesSnacks.stream()
                .map(SaleLineSnack::getSnackId)
                .toList();
        var snacks = findSnackPort.findAllById(snacksUUIDs);
        // Map snacks to with their IDs for easy access
        var snacksMap = snacks.stream()
                .collect(java.util.stream.Collectors.toMap(SnackView::id, snack -> snack));
        // Set snacks to sale lines snacks
        saleLinesSnacks.forEach(saleLineSnack -> {
            var snack = snacksMap.get(saleLineSnack.getSnackId());
            saleLineSnack.setSnackView(snack);
        });
        return saleLinesSnacks;
    }

    public SaleLineSnack saleLineSnackWithAllRelations(SaleLineSnack saleLineSnack) {
        var snack = findSnackPort.findById(saleLineSnack.getSnackId()).orElse(null);
        saleLineSnack.setSnackView(snack);
        return saleLineSnack;
    }

}
