package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.SaleLineTicket;

public interface SaveSaleLineTicketPort {
    SaleLineTicket save(SaleLineTicket saleLineTicket);
}
