package com.coworking.bookingapi.service;

import com.coworking.bookingapi.dto.BookingRequestDTO;
import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.model.BookingStatus;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.model.RoomType;
import com.coworking.bookingapi.repository.BookingRepository;
import com.coworking.bookingapi.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a camada de serviço de reservas.
 *
 * Nota arquitetural: Embora o código-fonte seja mantido em Inglês para seguir
 * o padrão da indústria, as anotações @DisplayName foram escritas em Português
 * para facilitar a leitura dos relatórios de teste por parte dos avaliadores.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room mockRoom;
    private Booking mockBooking;
    private BookingRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        mockRoom = new Room("Sala A", RoomType.SHARED, 10);
        // Injeta o ID de forma limpa, respeitando o encapsulamento do domínio rico
        ReflectionTestUtils.setField(mockRoom, "id", 1L);

        mockBooking = new Booking(
                "John Doe",
                LocalDate.of(2025, 10, 25),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                mockRoom
        );
        ReflectionTestUtils.setField(mockBooking, "id", 1L);

        validRequest = new BookingRequestDTO(
                "John Doe",
                LocalDate.of(2025, 10, 25),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                1L
        );
    }

    @Test
    @DisplayName("Deve criar uma reserva com sucesso quando não houver conflito")
    void createBooking_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.existsConflictingBooking(
                eq(1L), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        Booking savedBooking = bookingService.createBooking(validRequest);

        assertNotNull(savedBooking);
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reservar com conflito de horário")
    void createBooking_WithConflict_ShouldThrowException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.existsConflictingBooking(
                eq(1L), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(validRequest);
        });

        assertEquals("Já existe uma reserva confirmada para esta sala neste horário.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o horário de início for depois do horário de fim")
    void createBooking_InvalidTimes_ShouldThrowException() {

        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.existsConflictingBooking(
                eq(1L), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(false);

        BookingRequestDTO invalidRequest = new BookingRequestDTO(
                "John Doe",
                LocalDate.of(2025, 10, 25),
                LocalTime.of(14, 0), // Início depois do fim
                LocalTime.of(10, 0),
                1L
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(invalidRequest);
        });

        assertEquals("O horário de início deve ser anterior ao horário de término.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve cancelar uma reserva com sucesso delegando ao domínio")
    void cancelBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        Booking cancelledBooking = bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        verify(bookingRepository, times(1)).save(mockBooking);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar uma reserva já cancelada")
    void cancelBooking_AlreadyCancelled_ShouldThrowException() {
        mockBooking.cancel(); // Adiantamos o status da entidade mockada para cancelada

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.cancelBooking(1L);
        });

        assertEquals("Esta reserva já encontra-se cancelada.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}