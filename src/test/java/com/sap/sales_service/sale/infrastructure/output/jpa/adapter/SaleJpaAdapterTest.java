

package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.filter.SaleFilter;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.SaleMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaleJpaAdapterTest {

    @Mock
    private SaleEntityRepository saleEntityRepository;

    @Mock
    private SaleMapper saleMapper;

    @InjectMocks
    private SaleJpaAdapter adapter;

    private UUID saleId;
    private UUID clientId;
    private UUID cinemaId;

    private Sale domainSale;
    private SaleEntity entitySale;

    @BeforeEach
    void setUp() {
        saleId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        cinemaId = UUID.randomUUID();

        domainSale = new Sale(
                saleId,
                clientId,
                cinemaId,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                com.sap.common_lib.common.enums.sale.SaleStatusType.PENDING,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        entitySale = new SaleEntity();
        entitySale.setId(saleId);
        entitySale.setClientId(clientId);
        entitySale.setCinemaId(cinemaId);
        entitySale.setTotalAmount(new BigDecimal("100.00"));
        entitySale.setClaimedAmount(BigDecimal.ZERO);
        entitySale.setDiscountedAmount(BigDecimal.ZERO);
        entitySale.setStatus(com.sap.common_lib.common.enums.sale.SaleStatusType.PENDING);
        entitySale.setCreatedAt(domainSale.getCreatedAt());
        entitySale.setUpdatedAt(domainSale.getUpdatedAt());
        entitySale.setPaidAt(null);
    }

    @Test
    void findById_shouldMapAndReturn_whenExists() {
        // given
        given(saleEntityRepository.findById(saleId)).willReturn(Optional.of(entitySale));
        given(saleMapper.toDomain(entitySale)).willReturn(domainSale);

        // when
        Optional<Sale> result = adapter.findById(saleId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainSale);
        verify(saleEntityRepository).findById(saleId);
        verify(saleMapper).toDomain(entitySale);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // given
        given(saleEntityRepository.findById(saleId)).willReturn(Optional.empty());

        // when
        Optional<Sale> result = adapter.findById(saleId);

        // then
        assertThat(result).isEmpty();
        verify(saleEntityRepository).findById(saleId);
    }

    @Test
    void findByCustomerId_shouldMapAll() {
        // given
        var entityList = List.of(entitySale);
        var domainList = List.of(domainSale);
        given(saleEntityRepository.findByClientId(clientId)).willReturn(entityList);
        given(saleMapper.toDomain(entitySale)).willReturn(domainSale);

        // when
        List<Sale> result = adapter.findByCustomerId(clientId);

        // then
        assertThat(result).isEqualTo(domainList);
        verify(saleEntityRepository).findByClientId(clientId);
        verify(saleMapper).toDomain(entitySale);
    }

    @Test
    void search_shouldBuildSpec_andMapPage() {
        // given
        var filter = SaleFilter.builder().build();
        var pageable = PageRequest.of(0, 20);
        var entityPage = new PageImpl<>(List.of(entitySale), pageable, 1);
        given(saleEntityRepository.findAll(any(Specification.class), eq(pageable))).willReturn(entityPage);
        given(saleMapper.toDomain(entitySale)).willReturn(domainSale);

        // when
        Page<Sale> result = adapter.search(filter, 0);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(domainSale);

        // also capture that a Specification was actually built
        ArgumentCaptor<Specification<SaleEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(saleEntityRepository).findAll(specCaptor.capture(), eq(pageable));
        assertThat(specCaptor.getValue()).isNotNull();
    }

    @Test
    void save_shouldMapAndReturnSaved() {
        // given
        given(saleMapper.toEntity(domainSale)).willReturn(entitySale);
        given(saleEntityRepository.save(entitySale)).willReturn(entitySale);
        given(saleMapper.toDomain(entitySale)).willReturn(domainSale);

        // when
        Sale saved = adapter.save(domainSale);

        // then
        assertThat(saved).isEqualTo(domainSale);
        verify(saleMapper).toEntity(domainSale);
        verify(saleEntityRepository).save(entitySale);
        verify(saleMapper).toDomain(entitySale);
    }
}