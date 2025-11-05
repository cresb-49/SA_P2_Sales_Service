package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindCinemaPort;
import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.CinemaSalesSummaryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopCinemaSalesReportFactoryTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private FindCinemaPort findCinemaPort;

    @InjectMocks
    private TopCinemaSalesReportFactory factory;

    private CinemaSalesSummaryDTO summary(UUID cinemaId, BigDecimal totalAmount, long totalSales) {
        return new CinemaSalesSummaryDTO(
                cinemaId,
                totalAmount,
                totalSales,
                null
        );
    }

    @Test
    void withCinema_shouldAttachCinemaInformation() {
        // Arrange
        var dto = summary(CINEMA_ID, new BigDecimal("120.50"), 10L);
        var cinema = new CinemaView(CINEMA_ID, "Cinema UX");
        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema);

        // Act
        var result = factory.withCinema(dto);

        // Assert
        assertThat(result.cinema()).isEqualTo(cinema);
        verify(findCinemaPort).findById(CINEMA_ID);
    }

    @Test
    void withCinema_shouldAttachCinemaInformationForEachItem() {
        // Arrange
        var dto1 = summary(CINEMA_ID, new BigDecimal("200.00"), 20L);
        var dto2 = summary(OTHER_CINEMA_ID, new BigDecimal("100.00"), 15L);
        var cinema1 = new CinemaView(CINEMA_ID, "Cinema UX");
        var cinema2 = new CinemaView(OTHER_CINEMA_ID, "Cinema Center");

        when(findCinemaPort.findById(CINEMA_ID)).thenReturn(cinema1);
        when(findCinemaPort.findById(OTHER_CINEMA_ID)).thenReturn(cinema2);

        // Act
        var result = factory.withCinema(List.of(dto1, dto2));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().cinema()).isEqualTo(cinema1);
        assertThat(result.get(1).cinema()).isEqualTo(cinema2);
        verify(findCinemaPort).findById(CINEMA_ID);
        verify(findCinemaPort).findById(OTHER_CINEMA_ID);
    }
}
