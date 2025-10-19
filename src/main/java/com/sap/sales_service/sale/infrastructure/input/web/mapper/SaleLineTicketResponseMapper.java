package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.SaleLineTicketResponseDTO;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleLineTicketResponseMapper {

    private final TicketViewResponseMapper ticketViewResponseMapper;

    public SaleLineTicketResponseDTO toResponseDTO(SaleLineTicket domain) {
        if (domain == null) {
            return null;
        }
        return new SaleLineTicketResponseDTO(
                domain.getId(),
                domain.getSaleId(),
                domain.getQuantity(),
                domain.getUnitPrice(),
                domain.getTotalPrice(),
                domain.getStatus().name(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                ticketViewResponseMapper.toResponseDTO(domain.getTicketView())
        );
    }
}
