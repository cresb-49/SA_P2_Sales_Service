

package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineSnackEntity;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleLineSnackMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SnackSalesByCinemaMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleLineSnackEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaleLineSnackJpaAdapterTest {

    @Mock
    private SaleLineSnackEntityRepository repository;

    @Mock
    private SaleLineSnackMapper mapper;

    @Mock
    private SnackSalesByCinemaMapper snackSalesByCinemaMapper;

    private SaleLineSnackJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SaleLineSnackJpaAdapter(repository, mapper, snackSalesByCinemaMapper);
    }

    @Test
    @DisplayName("save() should map domain->entity, persist, and map back to domain")
    void save_shouldPersistAndMapBack() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        UUID snackId = UUID.randomUUID();
        var domain = new SaleLineSnack(id, saleId, snackId, 2, new BigDecimal("3.50"), new BigDecimal("7.00"));
        var entity = new SaleLineSnackEntity();
        var savedEntity = new SaleLineSnackEntity();
        var mappedBack = new SaleLineSnack(id, saleId, snackId, 2, new BigDecimal("3.50"), new BigDecimal("7.00"));

        given(mapper.toEntity(domain)).willReturn(entity);
        given(repository.save(entity)).willReturn(savedEntity);
        given(mapper.toDomain(savedEntity)).willReturn(mappedBack);

        // Act
        var result = adapter.save(domain);

        // Assert
        assertThat(result).isSameAs(mappedBack);
        verify(mapper).toEntity(domain);
        verify(repository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("findAllBySaleId() should map list of entities to domain list")
    void findAllBySaleId_shouldReturnMappedList() {
        // Arrange
        UUID saleId = UUID.randomUUID();
        var e1 = new SaleLineSnackEntity();
        var e2 = new SaleLineSnackEntity();
        var d1 = new SaleLineSnack(UUID.randomUUID(), saleId, UUID.randomUUID(), 1, new BigDecimal("1.00"), new BigDecimal("1.00"));
        var d2 = new SaleLineSnack(UUID.randomUUID(), saleId, UUID.randomUUID(), 2, new BigDecimal("2.00"), new BigDecimal("4.00"));

        given(repository.findAllBySaleId(saleId)).willReturn(List.of(e1, e2));
        given(mapper.toDomain(e1)).willReturn(d1);
        given(mapper.toDomain(e2)).willReturn(d2);

        // Act
        var result = adapter.findAllBySaleId(saleId);

        // Assert
        assertThat(result).containsExactly(d1, d2);
        verify(repository).findAllBySaleId(saleId);
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
    }

    @Test
    @DisplayName("findAllBySaleId() should return empty list when repository returns empty")
    void findAllBySaleId_shouldReturnEmptyList_whenNoData() {
        // Arrange
        UUID saleId = UUID.randomUUID();
        given(repository.findAllBySaleId(saleId)).willReturn(List.of());

        // Act
        var result = adapter.findAllBySaleId(saleId);

        // Assert
        assertThat(result).isEmpty();
        verify(repository).findAllBySaleId(saleId);
    }

    @Test
    @DisplayName("findById() should map Optional entity to Optional domain")
    void findById_shouldReturnMappedOptional() {
        // Arrange
        UUID id = UUID.randomUUID();
        var entity = new SaleLineSnackEntity();
        var domain = new SaleLineSnack(id, UUID.randomUUID(), UUID.randomUUID(), 1, new BigDecimal("2.00"), new BigDecimal("2.00"));

        given(repository.findById(id)).willReturn(Optional.of(entity));
        given(mapper.toDomain(entity)).willReturn(domain);

        // Act
        var result = adapter.findById(id);

        // Assert
        assertThat(result).containsSame(domain);
        verify(repository).findById(id);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById() should return empty when repository not found")
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        given(repository.findById(id)).willReturn(Optional.empty());

        // Act
        var result = adapter.findById(id);

        // Assert
        assertThat(result).isEmpty();
        verify(repository).findById(id);
    }
}