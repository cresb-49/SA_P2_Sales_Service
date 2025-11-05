package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports.CinemaSalesSummaryView;
import com.sap.sales_service.sale.infrastructure.output.jpa.mapper.CinemaSalesSummaryMapper;
import com.sap.sales_service.sale.infrastructure.output.jpa.repository.SaleEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopCinemaSalesJpaAdapterTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private SaleEntityRepository saleEntityRepository;

    @Mock
    private CinemaSalesSummaryMapper cinemaSalesSummaryMapper;

    @InjectMocks
    private TopCinemaSalesJpaAdapter adapter;

    @Test
    void getTopCinemaSales_shouldDelegateRepositoryAndMapResults() {
        // Arrange
        var from = LocalDateTime.of(2024, 5, 1, 0, 0);
        var to = LocalDateTime.of(2024, 5, 31, 23, 59);
        int limit = 3;

        CinemaSalesSummaryView view1 = mock(CinemaSalesSummaryView.class);
        CinemaSalesSummaryView view2 = mock(CinemaSalesSummaryView.class);
        var dto1 = new CinemaSalesSummaryDTO(CINEMA_ID, new BigDecimal("150.00"), 12L, null);
        var dto2 = new CinemaSalesSummaryDTO(OTHER_CINEMA_ID, new BigDecimal("120.00"), 9L, null);

        when(saleEntityRepository.findTopCinemaSales(from, to, limit)).thenReturn(List.of(view1, view2));
        when(cinemaSalesSummaryMapper.toDomain(view1)).thenReturn(dto1);
        when(cinemaSalesSummaryMapper.toDomain(view2)).thenReturn(dto2);

        // Act
        var result = adapter.getTopCinemaSales(from, to, limit);

        // Assert
        assertThat(result).containsExactly(dto1, dto2);
        verify(saleEntityRepository).findTopCinemaSales(from, to, limit);
        verify(cinemaSalesSummaryMapper).toDomain(view1);
        verify(cinemaSalesSummaryMapper).toDomain(view2);
    }
}
