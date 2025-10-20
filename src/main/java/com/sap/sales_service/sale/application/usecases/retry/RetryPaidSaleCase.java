package com.sap.sales_service.sale.application.usecases.retry;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.sales_service.sale.application.input.RetryPaidSaleCasePort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.application.ouput.SendPaidRequestPort;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RetryPaidSaleCase implements RetryPaidSaleCasePort {

    private final FindSalePort findSalePort;
    private final SaveSalePort saveSalePort;
    private final SendPaidRequestPort sendPaidRequestPort;
    private final SendNotificationPort sendNotificationPort;

    @Override
    public void retryPaidSale(UUID saleId) {
        Sale sale = findSalePort.findById(saleId)
                .orElseThrow(() -> new IllegalStateException("La venta con id " + saleId + " no fue encontrada"));
        if (sale.getPaidAt() != null) {
            throw new IllegalStateException("La venta con id " + saleId + " ya está pagada");
        }
        if (sale.getStatus() != SaleStatusType.PAID_ERROR) {
            throw new IllegalStateException("La venta con id " + saleId + " no está en estado de error de pago");
        }
        sale.pending();
        saveSalePort.save(sale);
        sendPaidRequestPort.sendPaidRequest(sale.getClientId(), sale.getId(), sale.getTotalAmount());
        if (sale.getClientId() != null) {
            sendNotificationPort.sendNotification(
                    sale.getClientId(),
                    "Se ha reintentado el pago de su venta con id " + saleId + ". Por favor, revise su método de pago."
            );
        }
    }
}
