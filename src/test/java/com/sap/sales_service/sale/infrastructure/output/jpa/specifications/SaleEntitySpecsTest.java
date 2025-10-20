package com.sap.sales_service.sale.infrastructure.output.jpa.specifications;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.sales_service.sale.domain.filter.SaleFilter;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class SaleEntitySpecsTest {

    private Root<SaleEntity> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;

    // Common predicates
    private Predicate pEq1, pEq2, pEq3, pLe1, pGe1, pLe2, pGe2, pLe3, pGe3, pAnd;

    @BeforeEach
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);

        // Predicates
        pEq1 = mock(Predicate.class);
        pEq2 = mock(Predicate.class);
        pEq3 = mock(Predicate.class);
        pLe1 = mock(Predicate.class);
        pGe1 = mock(Predicate.class);
        pLe2 = mock(Predicate.class);
        pGe2 = mock(Predicate.class);
        pLe3 = mock(Predicate.class);
        pGe3 = mock(Predicate.class);
        pAnd = mock(Predicate.class);

        // When cb.and is called with any array of predicates, return a combined predicate
        given(cb.and(any(Predicate.class), any(Predicate.class))).willReturn(pAnd);
        // varargs overload
        given(cb.and(any(Predicate[].class))).willReturn(pAnd);
    }

    @Test
    void byFilter_onlyClient_buildsEqualOnClientId() {
        var clientId = UUID.randomUUID();
        var filter = SaleFilter.builder().clientId(clientId).build();

        given(cb.equal(any(), eq(clientId))).willReturn(pEq1);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(any(), eq(clientId));

        verify(cb, never()).lessThanOrEqualTo(any(), any(LocalDateTime.class));
        verify(cb, never()).greaterThanOrEqualTo(any(), any(LocalDateTime.class));
    }

    @Test
    void byFilter_onlyCinema_buildsEqualOnCinemaId() {
        var cinemaId = UUID.randomUUID();
        var filter = SaleFilter.builder().cinemaId(cinemaId).build();

        given(cb.equal(any(), eq(cinemaId))).willReturn(pEq1);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(any(), eq(cinemaId));
    }

    @Test
    void byFilter_onlyStatus_buildsEqualOnStatusName() {
        var status = SaleStatusType.PAID;
        var filter = SaleFilter.builder().status(status).build();

        given(cb.equal(any(), eq(status.name()))).willReturn(pEq1);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).equal(any(), eq(status.name()));
    }

    @Test
    void byFilter_createdAtBounds_buildsBothPredicates_andCombines() {
        var min = LocalDateTime.now().minusDays(3);
        var max = LocalDateTime.now().plusDays(1);
        var filter = SaleFilter.builder()
                .minCreatedAt(min)
                .maxCreatedAt(max)
                .build();

        given(cb.lessThanOrEqualTo(any(), eq(max))).willReturn(pLe1);
        given(cb.greaterThanOrEqualTo(any(), eq(min))).willReturn(pGe1);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).lessThanOrEqualTo(any(), eq(max));
        verify(cb).greaterThanOrEqualTo(any(), eq(min));
        // Combined via cb.and(...)
        verify(cb, atLeastOnce()).and(any(Predicate.class), any(Predicate.class));
    }

    @Test
    void byFilter_updatedAtBounds_buildsBothPredicates() {
        var min = LocalDateTime.now().minusDays(10);
        var max = LocalDateTime.now().minusDays(1);
        var filter = SaleFilter.builder()
                .minUpdatedAt(min)
                .maxUpdatedAt(max)
                .build();

        given(cb.lessThanOrEqualTo(any(), eq(max))).willReturn(pLe2);
        given(cb.greaterThanOrEqualTo(any(), eq(min))).willReturn(pGe2);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).lessThanOrEqualTo(any(), eq(max));
        verify(cb).greaterThanOrEqualTo(any(), eq(min));
    }

    @Test
    void byFilter_paidAtBounds_buildsBothPredicates() {
        var min = LocalDateTime.now().minusDays(5);
        var max = LocalDateTime.now();
        var filter = SaleFilter.builder()
                .minPaidAt(min)
                .maxPaidAt(max)
                .build();

        given(cb.lessThanOrEqualTo(any(), eq(max))).willReturn(pLe3);
        given(cb.greaterThanOrEqualTo(any(), eq(min))).willReturn(pGe3);

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNotNull();
        verify(cb).lessThanOrEqualTo(any(), eq(max));
        verify(cb).greaterThanOrEqualTo(any(), eq(min));
    }

    @Test
    void byFilter_allNull_returnsNullOrNonThrowing() {
        var filter = SaleFilter.builder().build();

        var spec = SaleEntitySpecs.byFilter(filter);
        var result = spec.toPredicate(root, query, cb);

        assertThat(result).isNull();
        verify(cb, never()).equal(any(), any());
        verify(cb, never()).lessThanOrEqualTo(any(), any(LocalDateTime.class));
        verify(cb, never()).greaterThanOrEqualTo(any(), any(LocalDateTime.class));
    }
}