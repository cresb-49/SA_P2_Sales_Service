package com.sap.sales_service.tickets.infrastructure.output.jpa.specifications;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleEntity;
import com.sap.sales_service.sale.infrastructure.output.jpa.entity.SaleLineTicketEntity;
import com.sap.sales_service.tickets.domain.TicketFilter;
import com.sap.sales_service.tickets.infrastructure.output.jpa.entity.TicketEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketEntitySpecsTest {

    @Mock private Root<TicketEntity> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;
    @Mock private Subquery<UUID> uuidSubquery;
    @Mock private Root<SaleLineTicketEntity> saleLineTicketSubqueryRoot;
    @Mock private Root<SaleEntity> saleSubqueryRoot;

    private Path<UUID> saleLineTicketIdPath;
    private Path<UUID> cinemaFunctionIdPath;
    private Path<UUID> cinemaIdPath;
    private Path<UUID> cinemaRoomIdPath;
    private Path<UUID> seatIdPath;
    private Path<UUID> movieIdPath;
    private Path<Boolean> usedPath;
    private Path<LocalDateTime> createdAtPath;
    private Path<LocalDateTime> updatedAtPath;

    private Path<UUID> saleLineTicketIdSubqueryPath;
    private Path<UUID> saleLineTicketSaleIdPath;
    private Path<TicketStatusType> saleLineTicketStatusPath;
    private Path<UUID> saleRootIdPath;
    private Path<SaleStatusType> saleRootStatusPath;
    private Path<UUID> saleRootUserIdPath;

    private Predicate combinedPredicate;

    @BeforeEach
    void setUp() {
        saleLineTicketIdPath = mockPath();
        cinemaFunctionIdPath = mockPath();
        cinemaIdPath = mockPath();
        cinemaRoomIdPath = mockPath();
        seatIdPath = mockPath();
        movieIdPath = mockPath();
        usedPath = mockPath();
        createdAtPath = mockPath();
        updatedAtPath = mockPath();

        lenient().when(root.<UUID>get("saleLineTicketId")).thenReturn(saleLineTicketIdPath);
        lenient().when(root.<UUID>get("cinemaFunctionId")).thenReturn(cinemaFunctionIdPath);
        lenient().when(root.<UUID>get("cinemaId")).thenReturn(cinemaIdPath);
        lenient().when(root.<UUID>get("cinemaRoomId")).thenReturn(cinemaRoomIdPath);
        lenient().when(root.<UUID>get("seatId")).thenReturn(seatIdPath);
        lenient().when(root.<UUID>get("movieId")).thenReturn(movieIdPath);
        lenient().when(root.<Boolean>get("used")).thenReturn(usedPath);
        lenient().when(root.<LocalDateTime>get("createdAt")).thenReturn(createdAtPath);
        lenient().when(root.<LocalDateTime>get("updatedAt")).thenReturn(updatedAtPath);

        lenient().when(query.subquery(UUID.class)).thenReturn(uuidSubquery);
        lenient().when(uuidSubquery.from(SaleLineTicketEntity.class)).thenReturn(saleLineTicketSubqueryRoot);
        lenient().when(uuidSubquery.from(SaleEntity.class)).thenReturn(saleSubqueryRoot);
        lenient().when(uuidSubquery.select(any())).thenReturn(uuidSubquery);
        lenient().when(uuidSubquery.where(any(Predicate[].class))).thenReturn(uuidSubquery);

        saleLineTicketIdSubqueryPath = mockPath();
        saleLineTicketSaleIdPath = mockPath();
        saleLineTicketStatusPath = mockPath();
        saleRootIdPath = mockPath();
        saleRootStatusPath = mockPath();
        saleRootUserIdPath = mockPath();

        lenient().when(saleLineTicketSubqueryRoot.<UUID>get("id")).thenReturn(saleLineTicketIdSubqueryPath);
        lenient().when(saleLineTicketSubqueryRoot.<UUID>get("saleId")).thenReturn(saleLineTicketSaleIdPath);
        lenient().when(saleLineTicketSubqueryRoot.<TicketStatusType>get("status")).thenReturn(saleLineTicketStatusPath);
        lenient().when(saleSubqueryRoot.<UUID>get("id")).thenReturn(saleRootIdPath);
        lenient().when(saleSubqueryRoot.<SaleStatusType>get("status")).thenReturn(saleRootStatusPath);
        lenient().when(saleSubqueryRoot.<UUID>get("userId")).thenReturn(saleRootUserIdPath);

        combinedPredicate = mock(Predicate.class);
        lenient().when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(combinedPredicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(combinedPredicate);
    }

    private <T> Path<T> mockPath() {
        @SuppressWarnings("unchecked")
        Path<T> path = (Path<T>) mock(Path.class);
        return path;
    }

    @Test
    void byFilter_allNull_returnsNullPredicate() {
        var filter = TicketFilter.builder().build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isNull();
        verify(cb, never()).lessThanOrEqualTo(any(Path.class), any(LocalDateTime.class));
        verify(cb, never()).greaterThanOrEqualTo(any(Path.class), any(LocalDateTime.class));
    }

    @Test
    void byFilter_withDirectFields_buildsEqualPredicates() {
        var saleLineTicketId = UUID.randomUUID();
        var cinemaFunctionId = UUID.randomUUID();
        var cinemaId = UUID.randomUUID();
        var cinemaRoomId = UUID.randomUUID();
        var seatId = UUID.randomUUID();
        var movieId = UUID.randomUUID();
        var used = Boolean.TRUE;

        var saleLinePredicate = mock(Predicate.class);
        var cinemaFunctionPredicate = mock(Predicate.class);
        var cinemaPredicate = mock(Predicate.class);
        var cinemaRoomPredicate = mock(Predicate.class);
        var seatPredicate = mock(Predicate.class);
        var moviePredicate = mock(Predicate.class);
        var usedPredicate = mock(Predicate.class);

        when(cb.equal(saleLineTicketIdPath, saleLineTicketId)).thenReturn(saleLinePredicate);
        when(cb.equal(cinemaFunctionIdPath, cinemaFunctionId)).thenReturn(cinemaFunctionPredicate);
        when(cb.equal(cinemaIdPath, cinemaId)).thenReturn(cinemaPredicate);
        when(cb.equal(cinemaRoomIdPath, cinemaRoomId)).thenReturn(cinemaRoomPredicate);
        when(cb.equal(seatIdPath, seatId)).thenReturn(seatPredicate);
        when(cb.equal(movieIdPath, movieId)).thenReturn(moviePredicate);
        when(cb.equal(usedPath, used)).thenReturn(usedPredicate);

        var filter = TicketFilter.builder()
                .saleLineTicketId(saleLineTicketId)
                .cinemaFunctionId(cinemaFunctionId)
                .cinemaId(cinemaId)
                .cinemaRoomId(cinemaRoomId)
                .seatId(seatId)
                .movieId(movieId)
                .used(used)
                .build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isSameAs(combinedPredicate);
        verify(cb).equal(saleLineTicketIdPath, saleLineTicketId);
        verify(cb).equal(cinemaFunctionIdPath, cinemaFunctionId);
        verify(cb).equal(cinemaIdPath, cinemaId);
        verify(cb).equal(cinemaRoomIdPath, cinemaRoomId);
        verify(cb).equal(seatIdPath, seatId);
        verify(cb).equal(movieIdPath, movieId);
        verify(cb).equal(usedPath, used);
        verify(cb, never()).lessThanOrEqualTo(any(Path.class), any(LocalDateTime.class));
    }

    @Test
    void byFilter_withDateBounds_buildsRangePredicates() {
        var maxCreated = LocalDateTime.now();
        var minCreated = maxCreated.minusDays(2);
        var maxUpdated = maxCreated.plusDays(1);
        var minUpdated = maxCreated.minusDays(4);

        var leCreatedPredicate = mock(Predicate.class);
        var geCreatedPredicate = mock(Predicate.class);
        var leUpdatedPredicate = mock(Predicate.class);
        var geUpdatedPredicate = mock(Predicate.class);

        when(cb.lessThanOrEqualTo(createdAtPath, maxCreated)).thenReturn(leCreatedPredicate);
        when(cb.greaterThanOrEqualTo(createdAtPath, minCreated)).thenReturn(geCreatedPredicate);
        when(cb.lessThanOrEqualTo(updatedAtPath, maxUpdated)).thenReturn(leUpdatedPredicate);
        when(cb.greaterThanOrEqualTo(updatedAtPath, minUpdated)).thenReturn(geUpdatedPredicate);

        var filter = TicketFilter.builder()
                .maxCreatedAt(maxCreated)
                .minCreatedAt(minCreated)
                .maxUpdatedAt(maxUpdated)
                .minUpdatedAt(minUpdated)
                .build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isSameAs(combinedPredicate);
        verify(cb).lessThanOrEqualTo(createdAtPath, maxCreated);
        verify(cb).greaterThanOrEqualTo(createdAtPath, minCreated);
        verify(cb).lessThanOrEqualTo(updatedAtPath, maxUpdated);
        verify(cb).greaterThanOrEqualTo(updatedAtPath, minUpdated);
    }

    @Test
    void byFilter_withSaleStatus_filtersUsingSaleLineTicketSubquery() {
        var saleStatus = SaleStatusType.PAID;

        var joinPredicate = mock(Predicate.class);
        var saleStatusPredicate = mock(Predicate.class);
        var inPredicate = mock(Predicate.class);

        when(cb.equal(saleLineTicketSaleIdPath, saleRootIdPath)).thenReturn(joinPredicate);
        when(cb.equal(saleRootStatusPath, saleStatus)).thenReturn(saleStatusPredicate);
        when(saleLineTicketIdPath.in(uuidSubquery)).thenReturn(inPredicate);

        var filter = TicketFilter.builder()
                .saleStatus(saleStatus)
                .build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isSameAs(inPredicate);
        verify(query).subquery(UUID.class);
        verify(uuidSubquery).from(SaleLineTicketEntity.class);
        verify(uuidSubquery).from(SaleEntity.class);
        verify(uuidSubquery).select(saleLineTicketIdSubqueryPath);
        verify(cb).equal(saleLineTicketSaleIdPath, saleRootIdPath);
        verify(cb).equal(saleRootStatusPath, saleStatus);
        verify(uuidSubquery).where(joinPredicate, saleStatusPredicate);
        verify(saleLineTicketIdPath).in(uuidSubquery);
        verify(cb, never()).and(any(Predicate[].class));
    }

    @Test
    void byFilter_withTicketStatus_filtersUsingSaleLineTicketStatus() {
        var ticketStatus = TicketStatusType.RESERVED;

        var statusPredicate = mock(Predicate.class);
        var inPredicate = mock(Predicate.class);

        when(cb.equal(saleLineTicketStatusPath, ticketStatus)).thenReturn(statusPredicate);
        when(saleLineTicketIdPath.in(uuidSubquery)).thenReturn(inPredicate);

        var filter = TicketFilter.builder()
                .ticketStatus(ticketStatus)
                .build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isSameAs(inPredicate);
        verify(query).subquery(UUID.class);
        verify(uuidSubquery, atLeastOnce()).from(SaleLineTicketEntity.class);
        verify(uuidSubquery).select(saleLineTicketIdSubqueryPath);
        verify(cb).equal(saleLineTicketStatusPath, ticketStatus);
        verify(uuidSubquery).where(statusPredicate);
        verify(saleLineTicketIdPath).in(uuidSubquery);
    }

    @Test
    void byFilter_withUserId_filtersUsingSaleOwner() {
        var userId = UUID.randomUUID();

        var joinPredicate = mock(Predicate.class);
        var userPredicate = mock(Predicate.class);
        var inPredicate = mock(Predicate.class);

        when(cb.equal(saleLineTicketSaleIdPath, saleRootIdPath)).thenReturn(joinPredicate);
        when(cb.equal(saleRootUserIdPath, userId)).thenReturn(userPredicate);
        when(saleLineTicketIdPath.in(uuidSubquery)).thenReturn(inPredicate);

        var filter = TicketFilter.builder()
                .userId(userId)
                .build();

        var result = TicketEntitySpecs.byFilter(filter).toPredicate(root, query, cb);

        assertThat(result).isSameAs(inPredicate);
        verify(query).subquery(UUID.class);
        verify(uuidSubquery).from(SaleLineTicketEntity.class);
        verify(uuidSubquery).from(SaleEntity.class);
        verify(uuidSubquery).select(saleLineTicketIdSubqueryPath);
        verify(cb).equal(saleLineTicketSaleIdPath, saleRootIdPath);
        verify(cb).equal(saleRootUserIdPath, userId);
        verify(uuidSubquery).where(joinPredicate, userPredicate);
        verify(saleLineTicketIdPath).in(uuidSubquery);
    }
}
