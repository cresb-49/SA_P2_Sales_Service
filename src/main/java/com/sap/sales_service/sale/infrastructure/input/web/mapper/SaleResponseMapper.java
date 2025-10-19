package com.sap.sales_service.sale.infrastructure.input.web.mapper;

import com.sap.common_lib.dto.response.sales.SaleResponseDTO;
import com.sap.sales_service.sale.domain.Sale;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleResponseMapper {

    public final SaleLineSnackResponseMapper saleLineSnackResponseMapper;
    public final SaleLineTicketResponseMapper saleLineTicket;

    public SaleResponseDTO toResponseDTO(Sale domain) {
        if (domain == null) {
            return null;
        }
        return new SaleResponseDTO(
                domain.getId(),
                domain.getClientId(),
                domain.getTotalAmount(),
                domain.getStatus().toString(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getPaidAt(),
                domain.getSaleLineSnacks()
                        .stream()
                        .map(saleLineSnackResponseMapper::toResponseDTO)
                        .toList(),
                domain.getSaleLineTickets()
                        .stream()
                        .map(saleLineTicket::toResponseDTO)
                        .toList()
        );

    }

}
