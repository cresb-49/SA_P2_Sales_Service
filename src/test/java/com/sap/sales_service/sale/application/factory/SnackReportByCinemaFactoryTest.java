package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesByCinemaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnackReportByCinemaFactoryTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SNACK_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID OTHER_SNACK_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @Mock
    private FindSnackPort findSnackPort;

    @InjectMocks
    private SnackReportByCinemaFactory factory;

    private SnackView snackView(UUID id, String name) {
        return new SnackView(
                id,
                CINEMA_ID,
                name,
                new BigDecimal("9.99"),
                "http://image.png",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0)
        );
    }

    @Test
    void withSnackView_shouldAttachSnackView() {
        // Arrange
        var dto = new SnackSalesByCinemaDTO(CINEMA_ID, SNACK_ID, 10L, new BigDecimal("100.00"), null);
        var view = snackView(SNACK_ID, "Nachos");
        when(findSnackPort.findById(SNACK_ID)).thenReturn(Optional.of(view));

        // Act
        var result = factory.withSnackView(dto);

        // Assert
        assertThat(result.snack()).isEqualTo(view);
        verify(findSnackPort).findById(SNACK_ID);
    }

    @Test
    void withSnackView_shouldAttachSnackViewForEachDto() {
        // Arrange
        var dto1 = new SnackSalesByCinemaDTO(CINEMA_ID, SNACK_ID, 5L, new BigDecimal("50.00"), null);
        var dto2 = new SnackSalesByCinemaDTO(CINEMA_ID, OTHER_SNACK_ID, 8L, new BigDecimal("80.00"), null);
        var view1 = snackView(SNACK_ID, "Pop Corn");
        var view2 = snackView(OTHER_SNACK_ID, "Soda");
        when(findSnackPort.findAllById(List.of(SNACK_ID, OTHER_SNACK_ID))).thenReturn(List.of(view1, view2));

        // Act
        var result = factory.withSnackView(List.of(dto1, dto2));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().snack()).isEqualTo(view1);
        assertThat(result.get(1).snack()).isEqualTo(view2);
        verify(findSnackPort).findAllById(List.of(SNACK_ID, OTHER_SNACK_ID));
    }
}
