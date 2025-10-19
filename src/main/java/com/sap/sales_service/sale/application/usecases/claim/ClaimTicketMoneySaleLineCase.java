package com.sap.sales_service.sale.application.usecases.claim;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.input.ClaimTicketMoneySaleLineCasePort;
import com.sap.sales_service.sale.application.ouput.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ClaimTicketMoneySaleLineCase implements ClaimTicketMoneySaleLineCasePort {

    private final SaveSalePort saveSalePort;
    private final SaveSaleLineTicketPort saveSaleLineTicketPort;
    private final FindSalePort findSalePort;
    private final FindSaleLineTicketPort findSaleLineTicketPort;
    private final FindTicketPort findTicketPort;
    private final RefoundAmountRequestPort refoundAmountRequestPort;

    @Override
    public void claimTicketMoneySaleLine(UUID saleLineId) {
        var saleLineTicketOpt = findSaleLineTicketPort.findById(saleLineId).orElseThrow(
                () -> new NotFoundException("La linea de venta con id " + saleLineId + " no fue encontrada")
        );
        if (saleLineTicketOpt.getStatus() == TicketStatusType.CANCELLED) {
            throw new IllegalStateException("La linea de venta con id " + saleLineId + " ya fue cancelada");
        }
        var sale = findSalePort.findById(saleLineTicketOpt.getSaleId()).orElseThrow(
                () -> new NotFoundException("La venta con id " + saleLineTicketOpt.getSaleId() + " no fue encontrada")
        );
        if (sale.getStatus() != SaleStatusType.PAID) {
            throw new IllegalStateException("La venta con id " + sale.getId() + " no está pagada, no se puede reclamar el dinero del ticket");
        }
        var ticket = findTicketPort.findBySaleLineTicketId(saleLineId).orElseThrow(
                () -> new NotFoundException("El ticket para la linea de venta con id " + saleLineId + " no fue encontrado")
        );
        // Validated if the ticket is not used
        if (ticket.used()) {
            throw new IllegalStateException("El ticket para la linea de venta con id " + saleLineId + " ya fue utilizado");
        }
        // Update sale line ticket status to CANCELLED
        saleLineTicketOpt.cancel();
        // Calculate amount to refund
        var amountToRefund = saleLineTicketOpt.getTotalPrice();
        var userUUID = sale.getClientId();
        // Update sale claimed amount
        sale.sumClaimedAmount(amountToRefund);
        saveSaleLineTicketPort.save(saleLineTicketOpt);
        saveSalePort.save(sale);
        if (userUUID != null) {
            refoundAmountRequestPort.requestRefoundAmount(
                    amountToRefund,
                    userUUID,
                    "Reembolso por cancelación de ticket en la línea de venta con id " + saleLineId
            );
        }
    }
}
