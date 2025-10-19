package com.sap.sales_service.sale.application.usecases.cancel;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.application.factory.SaleLineTicketFactory;
import com.sap.sales_service.sale.application.input.CancelSaleCasePort;
import com.sap.sales_service.sale.application.ouput.*;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CancelSaleCase implements CancelSaleCasePort {

    private final FindSalePort findSalePort;
    private final SaveSalePort saveSalePort;
    private final FindSaleLineTicketPort findSaleLineTicketPort;
    private final FindSaleLineSnackPort findSaleLineSnackPort;
    private final SaveSaleLineTicketPort saveSaleLineTicketPort;
    private final RefoundAmountRequestPort refoundAmountRequestPort;
    private final SaleLineTicketFactory saleLineTicketFactory;
    private final SendNotificationPort sendNotificationPort;


    @Override
    public void cancelSaleById(UUID saleId) {
        Sale sale = findSalePort.findById(saleId).orElseThrow(
                () -> new IllegalStateException("La venta con id " + saleId + " no fue encontrada")
        );
        if (sale.getStatus() == SaleStatusType.CANCELLED) {
            throw new IllegalStateException("La venta con id " + saleId + " ya fue cancelada");
        }
        if (sale.getStatus() == SaleStatusType.PENDING) {
            throw new IllegalStateException("La venta con id " + saleId + " está pendiente y no puede ser cancelada por el momento");
        }
        // Si la venta esta pagada y solo tiene tickets sin usar, se procede a cancelar
        // Si la venta esta pagada y tiene tickets usados, no se puede cancelar
        // Si la venta esta pagada y tiene snacks, no se puede cancelar
        if (sale.getStatus() == SaleStatusType.PAID) {
            if (!findSaleLineSnackPort.findAllBySaleId(saleId).isEmpty()) {
                throw new IllegalStateException("La venta con id " + saleId + " tiene snacks y no puede ser cancelada");
            }
            var ticketsAmount = calculateClaimableAmount(saleId);
            sale.sumClaimedAmount(ticketsAmount);
            saveSalePort.save(sale);
            var claimableAmount = sale.getTotalAmount().subtract(sale.getClaimedAmount()).subtract(sale.getDiscountedAmount());
            var userUUID = sale.getClientId();
            sendRefundRequest(userUUID, claimableAmount, saleId);
            sendNotificationPort.sendNotification(
                    userUUID,
                    "Su venta con id " + saleId + " ha sido cancelada. Se ha reembolsado un monto de " + claimableAmount
            );
        } else {
            //Set cancelled status to sale and tickets
            sale.cancel();
            saveSalePort.save(sale);
            for (var saleLineSnack : findSaleLineTicketPort.findAllBySaleId(saleId)) {
                saleLineSnack.cancel();
                saveSaleLineTicketPort.save(saleLineSnack);
            }
            sendNotificationPort.sendNotification(
                    sale.getClientId(),
                    "Su venta con id " + saleId + " ha sido cancelada."
            );
        }
    }

    /**
     * Calculate the claimable amount for a sale by iterating through its sale line tickets.
     * If any ticket has been used, an exception is thrown.
     * Tickets that are not cancelled are marked as cancelled and their total price is added to the claimable amount.
     *
     * @param saleId The ID of the sale for which to calculate the claimable amount.
     * @return The total claimable amount for the sale.
     * @throws IllegalStateException if any ticket has been used.
     */
    private BigDecimal calculateClaimableAmount(UUID saleId) {
        var ticketsAmount = BigDecimal.ZERO;
        var saleLineTickets = saleLineTicketFactory.saleLineTicketsWithAllRelations(findSaleLineTicketPort.findAllBySaleId(saleId));
        for (var saleLineTicket : saleLineTickets) {
            if (saleLineTicket.getTicketView().used()) {
                throw new IllegalStateException("El ticket para la linea de venta con id " + saleLineTicket.getId() + " ya fue utilizado");
            }
            if (saleLineTicket.getStatus() != TicketStatusType.CANCELLED) {
                saleLineTicket.cancel();
                saveSaleLineTicketPort.save(saleLineTicket);
                ticketsAmount = ticketsAmount.add(saleLineTicket.getTotalPrice());
            }
        }
        return ticketsAmount;
    }

    /**
     * Sends a refund request for a specified amount to a user.
     *
     * @param userId The UUID of the user to whom the refund is to be sent.
     * @param amount The amount to be refunded.
     * @param saleId The ID of the sale associated with the refund.
     */
    private void sendRefundRequest(UUID userId, BigDecimal amount, UUID saleId) {
        if (userId == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        refoundAmountRequestPort.requestRefoundAmount(
                amount,
                userId,
                "Reembolso por cancelación de la venta con id " + saleId
        );
    }
}
