

package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleLineTicketMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleLineTicketEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaleLineTicketJpaAdapterTest {

    @Mock
    private SaleLineTicketEntityRepository repository;

    @Mock
    private SaleLineTicketMapper mapper;

    @InjectMocks
    private SaleLineTicketJpaAdapter adapter;

    private UUID saleId;
    private UUID lineId;

    @BeforeEach
    void init() {
        saleId = UUID.randomUUID();
        lineId = UUID.randomUUID();
    }

    @Test
    void save_shouldMapAndPersistAndReturnDomain() {
        // Given
        SaleLineTicket domainIn = org.mockito.Mockito.mock(SaleLineTicket.class);
        SaleLineTicketEntity entityIn = org.mockito.Mockito.mock(SaleLineTicketEntity.class);
        SaleLineTicketEntity entitySaved = org.mockito.Mockito.mock(SaleLineTicketEntity.class);
        SaleLineTicket domainOut = org.mockito.Mockito.mock(SaleLineTicket.class);

        given(mapper.toEntity(domainIn)).willReturn(entityIn);
        given(repository.save(entityIn)).willReturn(entitySaved);
        given(mapper.toDomain(entitySaved)).willReturn(domainOut);

        // When
        SaleLineTicket result = adapter.save(domainIn);

        // Then
        assertThat(result).isSameAs(domainOut);

        // Verify mapping & persistence order
        ArgumentCaptor<SaleLineTicketEntity> entityCaptor = ArgumentCaptor.forClass(SaleLineTicketEntity.class);
        verify(mapper).toEntity(domainIn);
        verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue()).isSameAs(entityIn);
        verify(mapper).toDomain(entitySaved);
    }

    @Test
    void findAllBySaleId_shouldMapEntitiesToDomainList() {
        // Given
        SaleLineTicketEntity e1 = org.mockito.Mockito.mock(SaleLineTicketEntity.class);
        SaleLineTicketEntity e2 = org.mockito.Mockito.mock(SaleLineTicketEntity.class);
        given(repository.findAllBySaleId(saleId)).willReturn(List.of(e1, e2));

        SaleLineTicket d1 = org.mockito.Mockito.mock(SaleLineTicket.class);
        SaleLineTicket d2 = org.mockito.Mockito.mock(SaleLineTicket.class);
        given(mapper.toDomain(e1)).willReturn(d1);
        given(mapper.toDomain(e2)).willReturn(d2);

        // When
        List<SaleLineTicket> result = adapter.findAllBySaleId(saleId);

        // Then
        assertThat(result).containsExactly(d1, d2);
        verify(repository).findAllBySaleId(saleId);
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    void findAllBySaleId_shouldReturnEmptyList_whenRepositoryReturnsEmpty() {
        // Given
        given(repository.findAllBySaleId(saleId)).willReturn(List.of());

        // When
        List<SaleLineTicket> result = adapter.findAllBySaleId(saleId);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findAllBySaleId(saleId);
    }

    @Test
    void findById_shouldReturnMappedDomain_whenFound() {
        // Given
        SaleLineTicketEntity entity = org.mockito.Mockito.mock(SaleLineTicketEntity.class);
        SaleLineTicket domain = org.mockito.Mockito.mock(SaleLineTicket.class);

        given(repository.findById(lineId)).willReturn(Optional.of(entity));
        given(mapper.toDomain(entity)).willReturn(domain);

        // When
        Optional<SaleLineTicket> result = adapter.findById(lineId);

        // Then
        assertThat(result).isPresent().containsSame(domain);
        verify(repository).findById(lineId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Given
        given(repository.findById(lineId)).willReturn(Optional.empty());

        // When
        Optional<SaleLineTicket> result = adapter.findById(lineId);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findById(lineId);
    }
}