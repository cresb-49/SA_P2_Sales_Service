package com.sap.sales_service.sale.application.usecases.updatestatesale;

import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.sale.application.input.UpdateStateSaleCasePort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.application.usecases.updatestatesale.dtos.UpdateStateSaleDTO;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateStateSaleCase implements UpdateStateSaleCasePort {

    private final FindSalePort findSalePort;
    private final SaveSalePort saveSalePort;

    @Override
    public void updateStateSale(UpdateStateSaleDTO updateStateSaleDTO) {
        Sale sale = findSalePort.findById(updateStateSaleDTO.saleId()).orElseThrow(
                () -> new NonRetryableBusinessException("Sale not found")
        );
        if (updateStateSaleDTO.paid()) {
            sale.markAsPaid();
            //Send notificaion to client
        } else {
            sale.markAsPaidError();
            //Send notificaion to client
        }
        // Persist changes
        saveSalePort.save(sale);
    }
}
