package com.sap.sales_service.sale.application.usecases.updatestatesale;

import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.sale.application.input.UpdateStateSaleCasePort;
import com.sap.sales_service.sale.application.ouput.*;
import com.sap.sales_service.sale.application.usecases.updatestatesale.dtos.UpdateStateSaleDTO;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateStateSaleCase implements UpdateStateSaleCasePort {

    private final FindSalePort findSalePort;
    private final SaveSalePort saveSalePort;
    private final SendNotificationPort sendNotificationPort;
    private final FindSaleLineTicketPort findSaleLineTicketPort;
    private final SaveSaleLineTicketPort saveSaleLineTicketPort;

    @Override
    public void updateStateSale(UpdateStateSaleDTO updateStateSaleDTO) {
        Sale sale = findSalePort.findById(updateStateSaleDTO.saleId()).orElseThrow(
                () -> new NonRetryableBusinessException("Sale not found")
        );
        if (updateStateSaleDTO.paid()) {
            sale.markAsPaid();
            this.setToPurchasedLinesTickets(sale.getId());
            sendNotificationToClient(
                    sale.getClientId(),
                    "Su pago para la venta con id " + sale.getId() + " ha sido exitoso. ¡Gracias por su compra!"
            );
        } else {
            sale.markAsPaidError();
            this.setToReservedLinesTickets(sale.getId());
            sendNotificationToClient(
                    sale.getClientId(),
                    "Hubo un problema con el pago de su venta con id " + sale.getId() + ". Por favor, intente nuevamente, los boletos han sido reservados."
            );
        }
        // Persist changes
        saveSalePort.save(sale);
    }

    /**
     * Sends a notification to the client if the userId and message are valid.
     *
     * @param userId  the UUID of the user
     * @param message the notification message
     */
    private void sendNotificationToClient(UUID userId, String message) {
        if (userId == null || message == null || message.isBlank()) {
            return;
        }
        sendNotificationPort.sendNotification(userId, message);
    }

    /**
     * Sets all sale line tickets of a sale to PURCHASED status.
     *
     * @param saleId the ID of the sale
     */
    private void setToPurchasedLinesTickets(UUID saleId) {
        var saleLineTickets = findSaleLineTicketPort.findAllBySaleId(saleId);
        saleLineTickets.forEach(saleLineTicket -> {
            saleLineTicket.purchase();
            saveSaleLineTicketPort.save(saleLineTicket);
        });
    }

    /**
     * Sets all sale line tickets of a sale to RESERVED status.
     * If a ticket cannot be reserved (e.g., already used), it is skipped.
     *
     * @param saleId the ID of the sale
     */
    private void setToReservedLinesTickets(UUID saleId) {
        var saleLineTickets = findSaleLineTicketPort.findAllBySaleId(saleId);
        saleLineTickets.forEach(saleLineTicket -> {
            try {
                saleLineTicket.reserve();
                saveSaleLineTicketPort.save(saleLineTicket);
            } catch (Exception ex) {
                // Pueden haber tickets que no se puedan reservar, por ejemplo si ya fueron usados
            }
        });
    }
}
