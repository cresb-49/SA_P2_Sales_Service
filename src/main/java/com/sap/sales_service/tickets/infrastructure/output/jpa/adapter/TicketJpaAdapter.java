package com.sap.sales_service.tickets.infrastructure.output.jpa.adapter;

import com.sap.sales_service.tickets.application.output.CountByFilterPort;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.application.output.SaveTicketPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import com.sap.sales_service.tickets.infrastructure.output.jpa.mapper.TicketMapper;
import com.sap.sales_service.tickets.infrastructure.output.jpa.repository.TicketEntityRepository;
import com.sap.sales_service.tickets.infrastructure.output.jpa.specifications.TicketEntitySpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketJpaAdapter implements FindingTicketPort, SaveTicketPort, CountByFilterPort, FindingByFilterPort {

    private final TicketEntityRepository ticketEntityRepository;
    private final TicketMapper ticketMapper;

    @Override
    public Optional<Ticket> findById(UUID id) {
        return ticketEntityRepository.findById(id)
                .map(ticketMapper::toDomain);
    }

    @Override
    public Optional<Ticket> findBySaleLineTicketId(UUID saleLineTicketId) {
        return ticketEntityRepository.findBySaleLineTicketId(saleLineTicketId)
                .map(ticketMapper::toDomain);
    }

    @Override
    public List<Ticket> findByIds(List<UUID> ids) {
        return ticketEntityRepository.findAllById(ids).stream()
                .map(ticketMapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findBySaleLineTicketIds(List<UUID> saleLineTicketIds) {
        return ticketEntityRepository.findBySaleLineTicketIdIn(saleLineTicketIds).stream()
                .map(ticketMapper::toDomain)
                .toList();
    }

    @Override
    public Ticket save(Ticket ticket) {
        var entity = ticketMapper.toEntity(ticket);
        var savedEntity = ticketEntityRepository.save(entity);
        return ticketMapper.toDomain(savedEntity);
    }

    @Override
    public Long countByFilter(TicketFilter filter) {
        var spec = TicketEntitySpecs.byFilter(filter);
        return ticketEntityRepository.count(spec);
    }

    @Override
    public Optional<Ticket> findBySpecificIdAndFilter(TicketFilter filter, UUID specificId) {
        if (specificId == null) {
            return Optional.empty();
        }
        var spec = Specification.allOf(TicketEntitySpecs.hasId(specificId),TicketEntitySpecs.byFilter(filter));
        return ticketEntityRepository.findOne(spec)
                .map(ticketMapper::toDomain);
    }

    @Override
    public List<Ticket> findByFilter(TicketFilter filter) {
        var spec = TicketEntitySpecs.byFilter(filter);
        var entities = ticketEntityRepository.findAll(spec);
        return entities.stream()
                .map(ticketMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Ticket> findByFilterPaged(TicketFilter filter, int page) {
        var spec = TicketEntitySpecs.byFilter(filter);
        var result = ticketEntityRepository.findAll(spec, PageRequest.of(page, 20));
        return result.map(ticketMapper::toDomain);
    }
}
