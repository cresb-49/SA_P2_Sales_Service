package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.TicketView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindTicketPort {
    Optional<TicketView> findBySaleLineTicketId(UUID saleLineTicketId);

    List<TicketView> findAllBySaleLineTicketIds(List<UUID> saleLineTicketIds);
}
