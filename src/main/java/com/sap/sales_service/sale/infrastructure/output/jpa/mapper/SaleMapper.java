package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleMapper {

    public Sale toDomain(SaleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Sale(
                entity.getId(),
                entity.getClientId(),
                entity.getCinemaId(),
                entity.getTotalAmount(),
                entity.getClaimedAmount(),
                entity.getDiscountedAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPaidAt()
        );
    }

    public SaleEntity toEntity(Sale domain) {
        if (domain == null) {
            return null;
        }
        return new SaleEntity(
                domain.getId(),
                domain.getClientId(),
                domain.getCinemaId(),
                domain.getTotalAmount(),
                domain.getClaimedAmount(),
                domain.getDiscountedAmount(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getPaidAt()
        );
    }
}
