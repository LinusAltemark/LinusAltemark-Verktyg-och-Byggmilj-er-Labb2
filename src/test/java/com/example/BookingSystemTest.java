package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingSystemTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingSystem bookingSystem;

    private final LocalDateTime now = LocalDateTime.of(2024, 2, 1, 12, 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(timeProvider.getCurrentTime()).thenReturn(now);
    }

    @Nested
    @DisplayName("Tests for bookRoom method")
    class BookRoomTests {

        @Test
        @DisplayName("Should successfully book a room when available")
        void shouldBookRoomWhenAvailable() throws NotificationException {
            LocalDateTime start = now.plusHours(1);
            LocalDateTime end = now.plusHours(2);
            String roomId = "room1";
            Room room = mock(Room.class);

            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
            when(room.isAvailable(start, end)).thenReturn(true);

            boolean result = bookingSystem.bookRoom(roomId, start, end);

            assertThat(result).isTrue();
            verify(room).addBooking(any(Booking.class));
            verify(roomRepository).save(room);
            verify(notificationService).sendBookingConfirmation(any(Booking.class));
        }

        @Test
        @DisplayName("Should not book a room when it's unavailable")
        void shouldNotBookUnavailableRoom() {
            LocalDateTime start = now.plusHours(1);
            LocalDateTime end = now.plusHours(2);
            String roomId = "room1";
            Room room = mock(Room.class);

            when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
            when(room.isAvailable(start, end)).thenReturn(false);

            boolean result = bookingSystem.bookRoom(roomId, start, end);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
                "2024-01-01T10:00, 2024-01-01T12:00",
                "2024-02-01T11:00, 2024-02-01T10:00"
        })
        @DisplayName("Should throw exception for invalid booking times")
        void shouldThrowExceptionForInvalidTimes(String startTime, String endTime) {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            String roomId = "room1";

            assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, start, end))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Tests for getAvailableRooms method")
    class GetAvailableRoomsTests {

        @Test
        @DisplayName("Should return list of available rooms")
        void shouldReturnAvailableRooms() {
            LocalDateTime start = now.plusHours(1);
            LocalDateTime end = now.plusHours(2);
            Room room1 = mock(Room.class);
            Room room2 = mock(Room.class);

            when(room1.isAvailable(start, end)).thenReturn(true);
            when(room2.isAvailable(start, end)).thenReturn(false);
            when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

            List<Room> availableRooms = bookingSystem.getAvailableRooms(start, end);

            assertThat(availableRooms).containsExactly(room1);
        }
    }

    @Nested
    @DisplayName("Tests for cancelBooking method")
    class CancelBookingTests {

        @Test
        @DisplayName("Should successfully cancel a future booking")
        void shouldCancelFutureBooking() throws NotificationException {
            String bookingId = UUID.randomUUID().toString();
            Room room = mock(Room.class);
            Booking booking = mock(Booking.class);

            when(room.hasBooking(bookingId)).thenReturn(true);
            when(room.getBooking(bookingId)).thenReturn(booking);
            when(booking.getStartTime()).thenReturn(now.plusHours(2));
            when(roomRepository.findAll()).thenReturn(List.of(room));

            boolean result = bookingSystem.cancelBooking(bookingId);

            assertThat(result).isTrue();
            verify(room).removeBooking(bookingId);
            verify(roomRepository).save(room);
            verify(notificationService).sendCancellationConfirmation(booking);
        }

        @Test
        @DisplayName("Should not cancel a non-existing booking")
        void shouldNotCancelNonExistingBooking() {
            when(roomRepository.findAll()).thenReturn(List.of());
            boolean result = bookingSystem.cancelBooking("invalid-id");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when canceling a past booking")
        void shouldThrowExceptionForPastBooking() {
            String bookingId = UUID.randomUUID().toString();
            Room room = mock(Room.class);
            Booking booking = mock(Booking.class);

            when(room.hasBooking(bookingId)).thenReturn(true);
            when(room.getBooking(bookingId)).thenReturn(booking);
            when(booking.getStartTime()).thenReturn(now.minusHours(1));
            when(roomRepository.findAll()).thenReturn(List.of(room));

            assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}

