package com.sap.sales_service.sale.infrastructure.output.jpa.mapper;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleLineTicketMapper {
    public SaleLineTicket toDomain(SaleLineTicketEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SaleLineTicket(
                entity.getId(),
                entity.getSaleId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public SaleLineTicketEntity toEntity(SaleLineTicket domain) {
        if (domain == null) {
            return null;
        }
        return new SaleLineTicketEntity(
                domain.getId(),
                domain.getSaleId(),
                domain.getQuantity(),
                domain.getUnitPrice(),
                domain.getTotalPrice(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
