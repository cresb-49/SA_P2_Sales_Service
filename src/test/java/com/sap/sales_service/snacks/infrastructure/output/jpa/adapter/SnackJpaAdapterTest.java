

package com.sap.sales_service.snacks.infrastructure.output.jpa.adapter;

import com.sap.sales_service.snacks.domain.Snack;
import com.sap.sales_service.snacks.domain.SnackFilter;
import com.sap.sales_service.snacks.infrastructure.output.jpa.mapper.SnackMapper;
import com.sap.sales_service.snacks.infrastructure.output.jpa.repository.SnackEntityRepository;
import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SnackJpaAdapterTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final String NAME = "Popcorn";
    private static final BigDecimal PRICE = BigDecimal.valueOf(10);

    @Mock private SnackEntityRepository repository;
    @Mock private SnackMapper mapper;

    @InjectMocks private SnackJpaAdapter adapter;

    private Snack domain;
    private SnackEntity entity;

    @BeforeEach
    void setUp() {
        domain = new Snack(CINEMA_ID, NAME, PRICE, true, "http://x");
        entity = new SnackEntity();
    }

    @Test
    void save_shouldMapToEntity_andReturnMappedDomain() {
        // Arrange
        SnackEntity savedEntity = new SnackEntity();
        Snack mapped = new Snack(CINEMA_ID, NAME + "2", PRICE, true, "http://y");
        given(mapper.toEntity(domain)).willReturn(entity);
        given(repository.save(entity)).willReturn(savedEntity);
        given(mapper.toDomain(savedEntity)).willReturn(mapped);

        // Act
        Snack result = adapter.save(domain);

        // Assert
        assertThat(result).isSameAs(mapped);
        InOrder inOrder = inOrder(mapper, repository);
        inOrder.verify(mapper).toEntity(domain);
        inOrder.verify(repository).save(entity);
        inOrder.verify(mapper).toDomain(savedEntity);
    }

    @Test
    void findById_shouldReturnMappedDomain_whenFound() {
        // Arrange
        given(repository.findById(ID)).willReturn(Optional.of(entity));
        given(mapper.toDomain(entity)).willReturn(domain);

        // Act
        Optional<Snack> result = adapter.findById(ID);

        // Assert
        assertThat(result).containsSame(domain);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(repository.findById(ID)).willReturn(Optional.empty());

        // Act
        Optional<Snack> result = adapter.findById(ID);

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(mapper);
    }

    @Test
    void findLikeNameAndCinemaId_shouldReturnMappedDomain_whenFound() {
        // Arrange
        given(repository.findByNameIgnoreCaseAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.of(entity));
        given(mapper.toDomain(entity)).willReturn(domain);

        // Act
        Optional<Snack> result = adapter.findLikeNameAndCinemaId(NAME, CINEMA_ID);

        // Assert
        assertThat(result).containsSame(domain);
    }

    @Test
    void findLikeNameAndCinemaId_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(repository.findByNameIgnoreCaseAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());

        // Act
        Optional<Snack> result = adapter.findLikeNameAndCinemaId(NAME, CINEMA_ID);

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(mapper);
    }

    @Test
    void findByIds_shouldMapAllEntitiesToDomain() {
        // Arrange
        UUID id2 = UUID.randomUUID();
        SnackEntity e1 = new SnackEntity();
        SnackEntity e2 = new SnackEntity();
        Snack d1 = new Snack(CINEMA_ID, NAME, PRICE, true, "http://1");
        Snack d2 = new Snack(CINEMA_ID, NAME + "2", PRICE, false, "http://2");
        given(repository.findAllById(List.of(ID, id2))).willReturn(List.of(e1, e2));
        given(mapper.toDomain(e1)).willReturn(d1);
        given(mapper.toDomain(e2)).willReturn(d2);

        // Act
        List<Snack> result = adapter.findByIds(List.of(ID, id2));

        // Assert
        assertThat(result).containsExactly(d1, d2);
    }

    @Test
    void searchByFilter_shouldQueryWithSpec_andMapPageContent() {
        // Arrange
        int page = 2;
        SnackEntity e = new SnackEntity();
        Snack mapped = new Snack(CINEMA_ID, NAME, PRICE, true, "http://img");
        Page<SnackEntity> repoPage = new PageImpl<>(List.of(e));
        given(repository.findAll(any(Specification.class), any(PageRequest.class))).willReturn(repoPage);
        given(mapper.toDomain(e)).willReturn(mapped);

        // Act
        Page<Snack> result = adapter.searchByFilter(new SnackFilter(null, null, null), page);

        // Assert
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findAll(any(Specification.class), captor.capture());
        assertThat(result.getContent()).containsExactly(mapped);
        var pr = captor.getValue();
        assertThat(pr.getPageNumber()).isEqualTo(page);
        assertThat(pr.getPageSize()).isEqualTo(20);
    }
}