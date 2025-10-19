package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SaleFactory {
    private final SaleLineTicketFactory saleLineTicketFactory;
    private final SaleLineSnackFactory saleLineSnackFactory;

    public Sale saleWithAllRelations(Sale sale) {
        Sale saleWithAllRelations = new Sale(sale);
        // Set sale lines tickets with all relations
        saleWithAllRelations.setSaleLineTickets(
                saleLineTicketFactory.saleLineTicketWithAllRelations(sale.getId())
        );
        // Set sale lines snacks with all relations
        saleWithAllRelations.setSaleLineSnacks(
                saleLineSnackFactory.saleLineSnackWithAllRelations(sale.getId())
        );
        return saleWithAllRelations;
    }

    public Page<Sale> salesWithAllRelations(Page<Sale> sales) {
        return sales.map(this::saleWithAllRelations);
    }

    public List<Sale> salesWithAllRelations(List<Sale> sales) {
        return sales.stream()
                .map(this::saleWithAllRelations)
                .toList();
    }
}
