package com.sap.sales_service.sale.infrastructure.output.web.adapter;

import com.sap.sales_service.common.infrastructure.output.web.dto.FunctionWebViewDTO;
import com.sap.sales_service.common.infrastructure.output.web.port.CinemaGatewayPort;
import com.sap.sales_service.sale.domain.dtos.FunctionView;
import com.sap.sales_service.sale.infrastructure.output.web.mapper.FunctionViewMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaleFunctionAdapterTest {

    @Mock
    private CinemaGatewayPort cinemaGatewayPort;

    @Mock
    private FunctionViewMapper functionViewMapper;

    @InjectMocks
    private SaleFunctionAdapter adapter;

    private UUID functionId;

    @BeforeEach
    void setup() {
        functionId = UUID.randomUUID();
    }

    @Test
    void findById_shouldDelegateToGateway_andMapWithMapper() {
        // given
        // We don't depend on the concrete internal DTO type, so return null and map from null.
        var expected = new FunctionView(
                functionId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(10),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2)
        );
        FunctionWebViewDTO webDto = new FunctionWebViewDTO(expected.id(), expected.movieId(), expected.cinemaId(), expected.cinemaRoomId(), java.math.BigDecimal.ONE, expected.startTime(), expected.endTime());
        given(cinemaGatewayPort.findFunctionById(eq(functionId))).willReturn(webDto);
        given(functionViewMapper.toDomain(eq(webDto))).willReturn(expected);

        // when
        var result = adapter.findById(functionId);

        // then
        assertThat(result).isEqualTo(expected);
        verify(cinemaGatewayPort).findFunctionById(eq(functionId));
        verify(functionViewMapper).toDomain(any());
    }

    @Test
    void findByFunctionIds_shouldDelegateToGateway_andMapListWithMapper() {
        // given
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        var expectedList = List.of(
                new FunctionView(
                        ids.get(0),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        BigDecimal.valueOf(2),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2)
                ),
                new FunctionView(
                        ids.get(1),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        BigDecimal.valueOf(3),
                        LocalDateTime.now().plusDays(2),
                        LocalDateTime.now().plusDays(2).plusHours(2)
                )
        );
        List<FunctionWebViewDTO> webDtos = List.of(
                new FunctionWebViewDTO(expectedList.get(0).id(), expectedList.get(0).movieId(), expectedList.get(0).cinemaId(), expectedList.get(0).cinemaRoomId(), java.math.BigDecimal.ONE, expectedList.get(0).startTime(), expectedList.get(0).endTime()),
                new FunctionWebViewDTO(expectedList.get(1).id(), expectedList.get(1).movieId(), expectedList.get(1).cinemaId(), expectedList.get(1).cinemaRoomId(), java.math.BigDecimal.ONE, expectedList.get(1).startTime(), expectedList.get(1).endTime())
        );
        given(cinemaGatewayPort.findFunctionsByIds(eq(ids))).willReturn(webDtos);
        given(functionViewMapper.toDomainList(eq(webDtos))).willReturn(expectedList);

        // when
        var result = adapter.findByFunctionIds(ids);

        // then
        assertThat(result).containsExactlyElementsOf(expectedList);
        verify(cinemaGatewayPort).findFunctionsByIds(eq(ids));
        verify(functionViewMapper).toDomainList(any());
    }
}
