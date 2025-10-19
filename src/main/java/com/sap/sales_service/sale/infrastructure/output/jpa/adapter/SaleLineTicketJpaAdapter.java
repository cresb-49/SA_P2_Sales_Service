package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.domain.SaleLineTicket;

public class SaleLineTicketJpaAdapter implements FindSaleLineTicketPort, SaveSaleLineTicketPort {
    @Override
    public SaleLineTicket save(SaleLineTicket saleLineTicket) {
        return null;
    }
}
