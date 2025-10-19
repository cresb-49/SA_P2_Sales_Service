package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineSnackEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleLineSnackMapper {
    public SaleLineSnack toDomain(SaleLineSnackEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SaleLineSnack(
                entity.getId(),
                entity.getSaleId(),
                entity.getSnackId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getTotalPrice()
        );
    }

    public SaleLineSnackEntity toEntity(SaleLineSnack domain) {
        if (domain == null) {
            return null;
        }
        return new SaleLineSnackEntity(
                domain.getId(),
                domain.getSaleId(),
                domain.getSnackId(),
                domain.getQuantity(),
                domain.getUnitPrice(),
                domain.getTotalPrice()
        );
    }
}
