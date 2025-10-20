package com.sap.sales_service.snacks.application.usecases.findsnack;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.domain.SnackFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FindSnackCaseTest {

    private static final UUID SNACK_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CINEMA_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final String NAME = "Popcorn";
    private static final BigDecimal PRICE = new BigDecimal("12.50");
    private static final String URL = "https://example.com/img.png";

    @Mock
    private FindingSnackPort findingSnackPort;

    @InjectMocks
    private FindSnackCase findSnackCase;

    private Snack sampleSnack;

    @BeforeEach
    void setUp() {
        sampleSnack = new Snack(
                SNACK_ID,
                CINEMA_ID,
                NAME,
                PRICE,
                true,
                URL,
                true,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void findById_shouldReturnSnack_whenExists() {
        // Arrange
        given(findingSnackPort.findById(SNACK_ID)).willReturn(Optional.of(sampleSnack));
        // Act
        Snack result = findSnackCase.findById(SNACK_ID);
        // Assert
        assertThat(result).isEqualTo(sampleSnack);
        verify(findingSnackPort).findById(SNACK_ID);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        // Arrange
        given(findingSnackPort.findById(SNACK_ID)).willReturn(Optional.empty());
        // Act
        // Assert
        assertThatThrownBy(() -> findSnackCase.findById(SNACK_ID))
                .isInstanceOf(NotFoundException.class);
        verify(findingSnackPort).findById(SNACK_ID);
    }

    @Test
    void search_shouldDelegateToPort_andReturnPage() {
        // Arrange
        SnackFilter filter = mock(SnackFilter.class);
        int page = 0;
        Page<Snack> expected = new PageImpl<>(List.of(sampleSnack));
        given(findingSnackPort.searchByFilter(filter, page)).willReturn(expected);
        // Act
        Page<Snack> result = findSnackCase.search(filter, page);
        // Assert
        assertThat(result).isSameAs(expected);
        verify(findingSnackPort).searchByFilter(filter, page);
    }

    @Test
    void findByIds_shouldDelegateToPort_andReturnList() {
        // Arrange
        List<UUID> ids = List.of(SNACK_ID);
        List<Snack> expected = List.of(sampleSnack);
        given(findingSnackPort.findByIds(ids)).willReturn(expected);
        // Act
        List<Snack> result = findSnackCase.findByIds(ids);
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(findingSnackPort).findByIds(ids);
    }
}
