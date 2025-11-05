package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.common_lib.dto.response.movie.MovieResponseDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.MovieGatewayPort;
import com.sap.sales_service.sale.domain.dtos.reports.views.MovieSummaryView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.MovieSummaryViewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketMovieReportAdapterTest {

    private static final UUID MOVIE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_MOVIE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private MovieGatewayPort movieGatewayPort;

    @Mock
    private MovieSummaryViewMapper movieSummaryViewMapper;

    @InjectMocks
    private TicketMovieReportAdapter adapter;

    @Test
    void findMoviesByIds_shouldReturnEmptyList_whenIdsNullOrEmpty() {
        // Act
        var resultWithNull = adapter.findMoviesByIds(null);
        var resultWithEmpty = adapter.findMoviesByIds(List.of());

        // Assert
        assertThat(resultWithNull).isEmpty();
        assertThat(resultWithEmpty).isEmpty();
        verify(movieGatewayPort, never()).getMoviesByIds(org.mockito.ArgumentMatchers.anyList());
        verify(movieSummaryViewMapper, never()).toViewList(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void findMoviesByIds_shouldDelegateToGatewayAndMapper() {
        // Arrange
        List<UUID> ids = List.of(MOVIE_ID, OTHER_MOVIE_ID);
        List<MovieResponseDTO> dtos = Collections.singletonList(null);
        List<MovieSummaryView> expected = List.of(
                new MovieSummaryView(MOVIE_ID, "Matrix"),
                new MovieSummaryView(OTHER_MOVIE_ID, "Inception")
        );

        when(movieGatewayPort.getMoviesByIds(ids)).thenReturn(dtos);
        when(movieSummaryViewMapper.toViewList(dtos)).thenReturn(expected);

        // Act
        var result = adapter.findMoviesByIds(ids);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(movieGatewayPort).getMoviesByIds(ids);
        verify(movieSummaryViewMapper).toViewList(dtos);
    }
}
