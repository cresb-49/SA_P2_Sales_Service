package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.filter.SaleFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSalePort {
    Optional<Sale> findById(UUID id);

    List<Sale> findByCustomerId(UUID customerId);

    Page<Sale> search(SaleFilter filter, int page);
}
