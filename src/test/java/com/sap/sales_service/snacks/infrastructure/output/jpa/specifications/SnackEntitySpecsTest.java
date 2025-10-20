package com.sap.sales_service.snacks.infrastructure.output.jpa.specifications;

import com.sap.sales_service.snacks.domain.SnackFilter;
import com.sap.sales_service.snacks.infrastructure.output.jpa.entity.SnackEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SnackEntitySpecsTest {

    private static final String NAME = "PopCorn";
    private static final Boolean ACTIVE = Boolean.TRUE;
    private static final UUID CINEMA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock private Root<SnackEntity> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;

    private Path<Object> anyPath() {
        @SuppressWarnings("unchecked")
        Path<Object> p = (Path<Object>) mock(Path.class);
        return p;
    }

    @BeforeEach
    void setupCommonRootStubs() {
        Path<Object> namePath = anyPath();
        Path<Object> activePath = anyPath();
        Path<Object> cinemaIdPath = anyPath();
        lenient().when(root.get("name")).thenReturn(namePath);
        lenient().when(root.get("active")).thenReturn(activePath);
        lenient().when(root.get("cinemaId")).thenReturn(cinemaIdPath);
    }

    @Test
    void byFilter_onlyName_buildsLikeOnLowerName() {
        // Arrange
        var filter = new SnackFilter(NAME, null, null);
        @SuppressWarnings("unchecked")
        Expression<String> lowerExpr = (Expression<String>) mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);
        given(cb.lower(any())).willReturn(lowerExpr);
        ArgumentCaptor<String> patternCaptor = ArgumentCaptor.forClass(String.class);
        given(cb.like(eq(lowerExpr), patternCaptor.capture())).willReturn(likePredicate);

        // Act
        var predicate = SnackEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        // Assert
        assertThat(predicate).isSameAs(likePredicate);
        assertThat(patternCaptor.getValue()).isEqualTo("%" + NAME.toLowerCase() + "%");
        verify(cb).lower(any());
    }

    @Test
    void byFilter_onlyActive_buildsEqualOnActive() {
        // Arrange
        var filter = new SnackFilter(null, ACTIVE, null);
        Predicate eqPredicate = mock(Predicate.class);
        given(cb.equal(any(Expression.class), eq(ACTIVE))).willReturn(eqPredicate);

        // Act
        var predicate = SnackEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        // Assert
        assertThat(predicate).isSameAs(eqPredicate);
    }

    @Test
    void byFilter_onlyCinema_buildsEqualOnCinemaId() {
        // Arrange
        var filter = new SnackFilter(null, null, CINEMA_ID);
        Predicate eqPredicate = mock(Predicate.class);
        given(cb.equal(any(Expression.class), eq(CINEMA_ID))).willReturn(eqPredicate);

        // Act
        var predicate = SnackEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        // Assert
        assertThat(predicate).isSameAs(eqPredicate);
    }

    @Test
    void byFilter_allFields_buildsAndOfAllPredicates() {
        // Arrange
        var filter = new SnackFilter(NAME, ACTIVE, CINEMA_ID);
        @SuppressWarnings("unchecked")
        Expression<String> lowerExpr = (Expression<String>) mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);
        Predicate activePredicate = mock(Predicate.class);
        Predicate cinemaPredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);
        given(cb.lower(any())).willReturn(lowerExpr);
        given(cb.like(eq(lowerExpr), any(String.class))).willReturn(likePredicate);
        given(cb.equal(any(Expression.class), eq(ACTIVE))).willReturn(activePredicate);
        given(cb.equal(any(Expression.class), eq(CINEMA_ID))).willReturn(cinemaPredicate);
        given(cb.and(any(Predicate.class), any(Predicate.class))).willReturn(andPredicate);

        // Act
        var predicate = SnackEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        // Assert
        assertThat(predicate).isSameAs(andPredicate);
    }

    @Test
    void byFilter_allNull_returnsNullPredicate() {
        // Arrange
        var filter = new SnackFilter(null, null, null);

        // Act
        var predicate = SnackEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        // Assert
        assertThat(predicate).isNull();
    }
}