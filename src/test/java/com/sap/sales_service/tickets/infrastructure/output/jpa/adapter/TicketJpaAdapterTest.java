package com.sap.sales_service.tickets.infrastructure.output.jpa.adapter;

import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import com.sap.sales_service.tickets.infrastructure.output.jpa.mapper.TicketMapper;
import com.sap.sales_service.tickets.infrastructure.output.jpa.repository.TicketEntityRepository;
import com.sap.sales_service.tickets.infrastructure.output.jpa.entity.TicketEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TicketJpaAdapterTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID SALE_LINE_TICKET_ID = UUID.randomUUID();
    private static final UUID CINEMA_FUNCTION_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID CINEMA_ROOM_ID = UUID.randomUUID();
    private static final UUID SEAT_ID = UUID.randomUUID();
    private static final UUID MOVIE_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private TicketEntityRepository ticketEntityRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketJpaAdapter adapter;

    private TicketEntity entity;
    private Ticket domain;

    @BeforeEach
    void setUp() {
        // Arrange
        entity = new TicketEntity(
                ID,
                SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                SEAT_ID,
                MOVIE_ID,
                false,
                NOW,
                NOW
        );
        domain = new Ticket(
                ID,
                SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                SEAT_ID,
                MOVIE_ID,
                false,
                NOW,
                NOW
        );
    }

    @Test
    void findById_shouldMap_whenFound() {
        // Arrange
        given(ticketEntityRepository.findById(ID)).willReturn(Optional.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findById(ID);
        // Assert
        assertThat(result).contains(domain);
        verify(ticketEntityRepository).findById(ID);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(ticketEntityRepository.findById(ID)).willReturn(Optional.empty());
        // Act
        var result = adapter.findById(ID);
        // Assert
        assertThat(result).isEmpty();
        verify(ticketEntityRepository).findById(ID);
    }

    @Test
    void findBySaleLineTicketId_shouldMap_whenFound() {
        // Arrange
        given(ticketEntityRepository.findBySaleLineTicketId(SALE_LINE_TICKET_ID)).willReturn(Optional.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findBySaleLineTicketId(SALE_LINE_TICKET_ID);
        // Assert
        assertThat(result).contains(domain);
        verify(ticketEntityRepository).findBySaleLineTicketId(SALE_LINE_TICKET_ID);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findByIds_shouldMapAll() {
        // Arrange
        var ids = List.of(ID);
        given(ticketEntityRepository.findAllById(ids)).willReturn(List.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findByIds(ids);
        // Assert
        assertThat(result).containsExactly(domain);
        verify(ticketEntityRepository).findAllById(ids);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findBySaleLineTicketIds_shouldMapAll() {
        // Arrange
        var ids = List.of(SALE_LINE_TICKET_ID);
        given(ticketEntityRepository.findBySaleLineTicketIdIn(ids)).willReturn(List.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findBySaleLineTicketIds(ids);
        // Assert
        assertThat(result).containsExactly(domain);
        verify(ticketEntityRepository).findBySaleLineTicketIdIn(ids);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findByCinemaFunctionIdAndSeatId_shouldMap_whenFound() {
        // Arrange
        given(ticketEntityRepository.findByCinemaFunctionIdAndSeatId(CINEMA_FUNCTION_ID, SEAT_ID))
                .willReturn(Optional.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findByCinemaFunctionIdAndSeatId(CINEMA_FUNCTION_ID, SEAT_ID);
        // Assert
        assertThat(result).contains(domain);
        verify(ticketEntityRepository).findByCinemaFunctionIdAndSeatId(CINEMA_FUNCTION_ID, SEAT_ID);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void save_shouldPersist_andMapBack() {
        // Arrange
        given(ticketMapper.toEntity(domain)).willReturn(entity);
        given(ticketEntityRepository.save(entity)).willReturn(entity);
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.save(domain);
        // Assert
        assertThat(result).isEqualTo(domain);
        verify(ticketMapper).toEntity(domain);
        verify(ticketEntityRepository).save(entity);
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void countByFilter_shouldDelegateToRepository() {
        // Arrange
        var filter = TicketFilter.builder().cinemaFunctionId(CINEMA_FUNCTION_ID).build();
        given(ticketEntityRepository.count(any(org.springframework.data.jpa.domain.Specification.class))).willReturn(5L);
        // Act
        var result = adapter.countByFilter(filter);
        // Assert
        assertThat(result).isEqualTo(5L);
        verify(ticketEntityRepository).count(any(org.springframework.data.jpa.domain.Specification.class));
    }

    @Test
    void findBySpecificIdAndFilter_shouldReturnEmpty_whenSpecificIdNull() {
        // Arrange
        var filter = TicketFilter.builder().build();
        // Act
        var result = adapter.findBySpecificIdAndFilter(filter, null);
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecificIdAndFilter_shouldMap_whenFound() {
        // Arrange
        var filter = TicketFilter.builder().cinemaFunctionId(CINEMA_FUNCTION_ID).build();
        given(ticketEntityRepository.findOne(any(org.springframework.data.jpa.domain.Specification.class))).willReturn(Optional.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findBySpecificIdAndFilter(filter, ID);
        // Assert
        assertThat(result).contains(domain);
        verify(ticketEntityRepository).findOne(any(org.springframework.data.jpa.domain.Specification.class));
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findByFilter_shouldMapList() {
        // Arrange
        var filter = TicketFilter.builder().cinemaId(CINEMA_ID).build();
        given(ticketEntityRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .willReturn(List.of(entity));
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findByFilter(filter);
        // Assert
        assertThat(result).containsExactly(domain);
        verify(ticketEntityRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        verify(ticketMapper).toDomain(entity);
    }

    @Test
    void findByFilterPaged_shouldMapPage() {
        // Arrange
        var filter = TicketFilter.builder().cinemaRoomId(CINEMA_ROOM_ID).build();
        Page<TicketEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        given(ticketEntityRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class)))
                .willReturn(page);
        given(ticketMapper.toDomain(entity)).willReturn(domain);
        // Act
        var result = adapter.findByFilterPaged(filter, 0);
        // Assert
        assertThat(result.getContent()).containsExactly(domain);
        verify(ticketEntityRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class));
        verify(ticketMapper).toDomain(entity);
    }
}