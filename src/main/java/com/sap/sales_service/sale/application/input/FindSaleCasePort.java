package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.find.dtos.SaleFilterDTO;
import com.sap.sales_service.sale.domain.Sale;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FindSaleCasePort {
    Sale findSaleById(UUID id);

    Page<Sale> findSalesByCustomerId(UUID customerId, SaleFilterDTO saleFilterDTO, int page);

    Page<Sale> findSalesByCinemaId(UUID cinemaId, SaleFilterDTO saleFilterDTO, int page);

    Page<Sale> findAllSales(SaleFilterDTO saleFilterDTO,int page);
}
