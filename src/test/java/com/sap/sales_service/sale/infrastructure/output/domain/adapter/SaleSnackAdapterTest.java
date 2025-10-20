package com.sap.sales_service.sale.infrastructure.output.domain.adapter;

import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.infrastructure.output.domain.mapper.SaleSnackViewMapper;
import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;
import com.sap.sales_service.snacks.infrastructure.input.service.port.SnackGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SaleSnackAdapter}.
 *
 * Notes about style:
 *  - Only stub what each test actually uses to avoid UnnecessaryStubbing exceptions.
 *  - Verify delegation to gateway and mapping are correct.
 */
class SaleSnackAdapterTest {

    private SnackGatewayPort snackGatewayPort;
    private SaleSnackViewMapper snackViewMapper;

    private SaleSnackAdapter adapter;

    @BeforeEach
    void setUp() {
        snackGatewayPort = mock(SnackGatewayPort.class);
        snackViewMapper = mock(SaleSnackViewMapper.class);
        adapter = new SaleSnackAdapter(snackGatewayPort, snackViewMapper);
    }

    // ---- findById ----

    @Test
    void findById_shouldReturnMappedView_whenSnackExists() {
        // given
        UUID id = UUID.randomUUID();
        SnackInternalView snackDomain = new SnackInternalView(id, UUID.randomUUID(), "Popcorn", BigDecimal.valueOf(9.99), "https://img/popcorn.png", LocalDateTime.now(), LocalDateTime.now());
        SnackView mapped = new SnackView(id, UUID.randomUUID(), "Popcorn", BigDecimal.valueOf(9.99), "https://img/popcorn.png", LocalDateTime.now(), LocalDateTime.now());

        given(snackGatewayPort.findById(id)).willReturn(Optional.of(snackDomain));
        given(snackViewMapper.toView(snackDomain)).willReturn(mapped);

        // when
        Optional<SnackView> result = adapter.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mapped);
        verify(snackGatewayPort).findById(id);
        verify(snackViewMapper).toView(snackDomain);
        verifyNoMoreInteractions(snackGatewayPort, snackViewMapper);
    }

    @Test
    void findById_shouldReturnEmpty_whenSnackNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(snackGatewayPort.findById(id)).willReturn(Optional.empty());

        // when
        Optional<SnackView> result = adapter.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(snackGatewayPort).findById(id);
        verifyNoInteractions(snackViewMapper);
        verifyNoMoreInteractions(snackGatewayPort);
    }

    // ---- findAllById ----

    @Test
    void findAllById_shouldDelegateAndMapList() {
        // given
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<SnackInternalView> domainList = List.of(
            new SnackInternalView(UUID.randomUUID(), UUID.randomUUID(), "NachosD", BigDecimal.valueOf(4.40), "https://img/nachos-d.png", LocalDateTime.now(), LocalDateTime.now()),
            new SnackInternalView(UUID.randomUUID(), UUID.randomUUID(), "SodaD", BigDecimal.valueOf(2.10), "https://img/soda-d.png", LocalDateTime.now(), LocalDateTime.now())
        );
        List<SnackView> mappedList = List.of(
                new SnackView(UUID.randomUUID(), UUID.randomUUID(), "Nachos", BigDecimal.valueOf(5.50), "https://img/nachos.png", LocalDateTime.now(), LocalDateTime.now()),
                new SnackView(UUID.randomUUID(), UUID.randomUUID(), "Soda", BigDecimal.valueOf(3.25), "https://img/soda.png", LocalDateTime.now(), LocalDateTime.now())
        );

        given(snackGatewayPort.findByIds(ids)).willReturn(domainList);
        given(snackViewMapper.toViewList(domainList)).willReturn(mappedList);

        // when
        List<SnackView> result = adapter.findAllById(ids);

        // then
        assertThat(result).isEqualTo(mappedList);

        ArgumentCaptor<List<UUID>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(snackGatewayPort).findByIds(idsCaptor.capture());
        assertThat(idsCaptor.getValue()).containsExactlyElementsOf(ids);

        verify(snackViewMapper).toViewList(domainList);
        verifyNoMoreInteractions(snackGatewayPort, snackViewMapper);
    }
}