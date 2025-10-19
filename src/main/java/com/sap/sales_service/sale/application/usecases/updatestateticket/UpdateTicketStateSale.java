package com.sap.sales_service.sale.application.usecases.updatestateticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.sale.application.input.UpdateTicketStateSalePort;
import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateTicketStateSale implements UpdateTicketStateSalePort {

    private final SaveSaleLineTicketPort saveSaleLineTicketPort;
    private final FindSaleLineTicketPort findSaleLineTicketPort;
    private final SendNotificationPort sendNotificationPort;
    private final FindSalePort findSalePort;

    @Override
    public void updateTicketState(UUID saleLineTicketId, TicketStatusType newStatus, String message) {
        SaleLineTicket saleLineTicket = findSaleLineTicketPort.findById(saleLineTicketId)
                .orElseThrow(() -> new NonRetryableBusinessException("La línea de venta de ticket con id " + saleLineTicketId + " no fue encontrada"));
        try {
            if (newStatus == TicketStatusType.IN_USE) {
                saleLineTicket.use();
                // save changes
                saveSaleLineTicketPort.save(saleLineTicket);
                sendNotificationToClient(saleLineTicket.getSaleId(), message);
            } else if (newStatus == TicketStatusType.RESERVED) {
                saleLineTicket.reserve();
                // save changes
                saveSaleLineTicketPort.save(saleLineTicket);
                sendNotificationToClient(saleLineTicket.getSaleId(), message);
            } else {
                sendNotificationToClient(saleLineTicket.getSaleId(), "La actualización de estado no es válida.");
            }
        } catch (IllegalStateException e) {
            sendNotificationToClient(saleLineTicket.getSaleId(), message + " ," + e.getMessage());
        }
    }

    /**
     * Sends a notification to the client associated with the sale.
     *
     * @param saleId  the UUID of the sale
     * @param message the notification message
     */
    private void sendNotificationToClient(UUID saleId, String message) {
        var sale = findSalePort.findById(saleId).orElse(null);
        if (sale != null) {
            if (sale.getClientId() != null) {
                sendNotificationPort.sendNotification(
                        sale.getClientId(),
                        message
                );
            }
        }
    }
}
