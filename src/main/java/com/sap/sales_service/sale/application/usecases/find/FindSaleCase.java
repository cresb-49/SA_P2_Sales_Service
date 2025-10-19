package com.sap.sales_service.sale.application.usecases.find;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.factory.SaleFactory;
import com.sap.sales_service.sale.application.input.FindSaleCasePort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.usecases.find.dtos.SaleFilterDTO;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindSaleCase implements FindSaleCasePort {

    private final FindSalePort findSalePort;
    private final SaleFactory saleFactory;

    @Override
    public Sale findSaleById(UUID id) {
        var sale = findSalePort.findById(id).orElseThrow(
                () -> new NotFoundException("Venta no encontrada")
        );
        return saleFactory.saleWithAllRelations(sale);
    }

    @Override
    public Page<Sale> findSalesByCustomerId(UUID customerId, SaleFilterDTO saleFilterDTO, int page) {
        var filter = saleFilterDTO.withClientId(customerId);
        var result = findSalePort.search(filter, page);
        return saleFactory.salesWithAllRelations(result);
    }

    @Override
    public Page<Sale> findSalesByCinemaId(UUID cinemaId, SaleFilterDTO saleFilterDTO, int page) {
        var filter = saleFilterDTO.withCinemaId(cinemaId);
        var result = findSalePort.search(filter, page);
        return saleFactory.salesWithAllRelations(result);
    }

    @Override
    public Page<Sale> findAllSales(SaleFilterDTO saleFilterDTO, int page) {
        var filter = saleFilterDTO.toDomain();
        var result = findSalePort.search(filter, page);
        return saleFactory.salesWithAllRelations(result);
    }
}
