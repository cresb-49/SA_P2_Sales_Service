package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.SaleLineTicket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSaleLineTicketPort {
    List<SaleLineTicket> findAllBySaleId(UUID saleId);
    Optional<SaleLineTicket> findById(UUID id);
}
