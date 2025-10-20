

package com.sap.sales_service.tickets.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketTest {

    private static final UUID ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SALE_LINE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID FUNCTION_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID CINEMA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID ROOM_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID SEAT_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    private static final UUID MOVIE_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    @Test
    void constructor_shouldInitializeFields_andDefaultValues() {
        // Arrange
        var before = LocalDateTime.now().minusSeconds(1);

        // Act
        var ticket = new Ticket(SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID);

        // Assert
        assertThat(ticket.getId()).isNotNull();
        assertThat(ticket.getSaleLineTicketId()).isEqualTo(SALE_LINE_ID);
        assertThat(ticket.getCinemaFunctionId()).isEqualTo(FUNCTION_ID);
        assertThat(ticket.getCinemaId()).isEqualTo(CINEMA_ID);
        assertThat(ticket.getCinemaRoomId()).isEqualTo(ROOM_ID);
        assertThat(ticket.getSeatId()).isEqualTo(SEAT_ID);
        assertThat(ticket.getMovieId()).isEqualTo(MOVIE_ID);
        assertThat(ticket.isUsed()).isFalse();
        assertThat(ticket.getCreatedAt()).isAfter(before);
        assertThat(Duration.between(ticket.getCreatedAt(), ticket.getUpdatedAt()).abs().getSeconds()).isLessThanOrEqualTo(1);
    }

    @Test
    void markAsUsed_shouldSetUsedTrue_andUpdateTimestamp() {
        // Arrange
        var ticket = new Ticket(SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID);
        var previousUpdatedAt = ticket.getUpdatedAt();

        // Act
        ticket.markAsUsed();

        // Assert
        assertThat(ticket.isUsed()).isTrue();
        assertThat(ticket.getUpdatedAt()).isAfter(previousUpdatedAt);
    }

    @Test
    void markAsUsed_shouldThrow_whenAlreadyUsed() {
        // Arrange
        var ticket = new Ticket(SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID);
        ticket.markAsUsed();

        // Act
        var thrown = assertThrows(RuntimeException.class, ticket::markAsUsed);

        // Assert
        assertThat(thrown).isInstanceOf(RuntimeException.class);
    }

    @Test
    void equals_shouldBeTrue_whenSameId() {
        // Arrange
        var now = LocalDateTime.now();
        var t1 = new Ticket(ID_1, SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID, false, now, now);
        var t2 = new Ticket(ID_1, SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID, true, now.minusDays(1), now.minusDays(1));

        // Act
        var result = t1.equals(t2);

        // Assert
        assertThat(result).isTrue();
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    void equals_shouldBeFalse_whenDifferentId() {
        // Arrange
        var now = LocalDateTime.now();
        var t1 = new Ticket(ID_1, SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID, false, now, now);
        var t2 = new Ticket(ID_2, SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID, false, now, now);

        // Act
        var result = t1.equals(t2);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void equals_shouldBeFalse_whenNullOrDifferentClass() {
        // Arrange
        var now = LocalDateTime.now();
        var ticket = new Ticket(ID_1, SALE_LINE_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, SEAT_ID, MOVIE_ID, false, now, now);

        // Act
        var resultNull = ticket.equals(null);
        var resultOther = ticket.equals("other");

        // Assert
        assertThat(resultNull).isFalse();
        assertThat(resultOther).isFalse();
    }
}