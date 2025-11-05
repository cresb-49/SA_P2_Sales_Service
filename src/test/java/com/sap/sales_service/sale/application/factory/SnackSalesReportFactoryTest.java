package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSnackPort;
import com.sap.sales_service.sale.domain.dtos.SnackView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnackSalesReportFactoryTest {

    private static final UUID SNACK_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID OTHER_SNACK_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Mock
    private FindSnackPort findSnackPort;

    @InjectMocks
    private SnackSalesReportFactory factory;

    private SnackView snackView(UUID id, String name) {
        return new SnackView(
                id,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                name,
                new BigDecimal("10.50"),
                "http://image.png",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0)
        );
    }

    @Test
    void withSnackView_shouldAttachSnackInformation() {
        // Arrange
        var dto = new SnackSalesSummaryDTO(SNACK_ID, 12L, null);
        var view = snackView(SNACK_ID, "Pop Corn");
        when(findSnackPort.findById(SNACK_ID)).thenReturn(Optional.of(view));

        // Act
        var result = factory.withSnackView(dto);

        // Assert
        assertThat(result.snack()).isEqualTo(view);
        verify(findSnackPort).findById(SNACK_ID);
    }

    @Test
    void withSnackView_shouldReturnSameList_whenEmptyInput() {
        // Arrange
        var empty = List.<SnackSalesSummaryDTO>of();

        // Act
        var result = factory.withSnackView(empty);

        // Assert
        assertThat(result).isSameAs(empty);
        verifyNoInteractions(findSnackPort);
    }

    @Test
    void withSnackView_shouldAttachSnackInformationForAllItems() {
        // Arrange
        var dto1 = new SnackSalesSummaryDTO(SNACK_ID, 5L, null);
        var dto2 = new SnackSalesSummaryDTO(OTHER_SNACK_ID, 8L, null);
        var view1 = snackView(SNACK_ID, "Hot Dog");
        var view2 = snackView(OTHER_SNACK_ID, "Nachos");
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
