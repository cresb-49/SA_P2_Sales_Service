

package com.sap.sales_service.snacks.infrastructure.input.service;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.infrastructure.input.service.dtos.SnackInternalView;
import com.sap.sales_service.snacks.infrastructure.input.service.mappers.SnackInternalViewMapper;
import com.sap.sales_service.snacks.infrastructure.output.jpa.adapter.SnackJpaAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SnackGatewayTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final String NAME = "Popcorn";
    private static final String NAME2 = "Soda";
    private static final String URL = "https://cdn/img.png";
    private static final String URL2 = "https://cdn/img2.png";
    private static final BigDecimal PRICE = new BigDecimal("10.00");
    private static final BigDecimal PRICE2 = new BigDecimal("5.50");

    @Mock private SnackJpaAdapter snackJpaAdapter;
    @Mock private SnackInternalViewMapper snackInternalViewMapper;

    @InjectMocks private SnackGateway snackGateway;

    private Snack domainSnack1;
    private Snack domainSnack2;
    private SnackInternalView view1;
    private SnackInternalView view2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        domainSnack1 = new Snack(ID, CINEMA_ID, NAME, PRICE, true, URL, true, now, now);
        domainSnack2 = new Snack(ID2, CINEMA_ID, NAME2, PRICE2, false, URL2, true, now, now);
        view1 = new SnackInternalView(ID, CINEMA_ID, NAME, PRICE, URL, now, now);
        view2 = new SnackInternalView(ID2, CINEMA_ID, NAME2, PRICE2, URL2, now, now);
    }

    @Test
    @DisplayName("findById -> returns mapped view when found")
    void findById_shouldReturnMappedView_whenFound() {
        // Arrange
        given(snackJpaAdapter.findById(ID)).willReturn(Optional.of(domainSnack1));
        given(snackInternalViewMapper.toView(domainSnack1)).willReturn(view1);
        // Act
        Optional<SnackInternalView> result = snackGateway.findById(ID);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(view1);
    }

    @Test
    @DisplayName("findById -> returns empty when not found")
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(snackJpaAdapter.findById(ID)).willReturn(Optional.empty());
        // Act
        Optional<SnackInternalView> result = snackGateway.findById(ID);
        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(snackInternalViewMapper);
    }

    @Test
    @DisplayName("findByIds -> returns mapped list when adapter returns domains")
    void findByIds_shouldReturnMappedList_whenFound() {
        // Arrange
        List<UUID> ids = List.of(ID, ID2);
        List<Snack> domains = List.of(domainSnack1, domainSnack2);
        List<SnackInternalView> views = List.of(view1, view2);
        given(snackJpaAdapter.findByIds(ids)).willReturn(domains);
        given(snackInternalViewMapper.toListView(domains)).willReturn(views);
        // Act
        List<SnackInternalView> result = snackGateway.findByIds(ids);
        // Assert
        assertThat(result).containsExactlyElementsOf(views);
    }

    @Test
    @DisplayName("findByIds -> returns empty list when adapter returns empty")
    void findByIds_shouldReturnEmptyList_whenAdapterReturnsEmpty() {
        // Arrange
        List<UUID> ids = List.of(ID, ID2);
        given(snackJpaAdapter.findByIds(ids)).willReturn(List.of());
        given(snackInternalViewMapper.toListView(List.of())).willReturn(List.of());
        // Act
        List<SnackInternalView> result = snackGateway.findByIds(ids);
        // Assert
        assertThat(result).isEmpty();
    }
}