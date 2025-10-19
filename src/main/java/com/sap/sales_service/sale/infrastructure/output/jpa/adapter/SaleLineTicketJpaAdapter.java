package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleLineTicketMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleLineTicketEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleLineTicketJpaAdapter implements FindSaleLineTicketPort, SaveSaleLineTicketPort {

    private final SaleLineTicketEntityRepository saleLineTicketEntityRepository;
    private final SaleLineTicketMapper saleLineTicketMapper;


    @Override
    public SaleLineTicket save(SaleLineTicket saleLineTicket) {
        var entity = saleLineTicketMapper.toEntity(saleLineTicket);
        var savedEntity = saleLineTicketEntityRepository.save(entity);
        return saleLineTicketMapper.toDomain(savedEntity);
    }

    @Override
    public List<SaleLineTicket> findAllBySaleId(UUID saleId) {
        return saleLineTicketEntityRepository.findAllBySaleId(saleId).stream()
                .map(saleLineTicketMapper::toDomain).toList();
    }

    @Override
    public Optional<SaleLineTicket> findById(UUID id) {
        return saleLineTicketEntityRepository.findById(id)
                .map(saleLineTicketMapper::toDomain);
    }
}
