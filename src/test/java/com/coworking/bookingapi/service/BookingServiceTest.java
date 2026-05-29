package com.coworking.bookingapi.service;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @BeforeEach
    void setUp() {
        // Prepara os dados falsos antes de cada teste rodar
        mockRoom = new Room("Sala A", RoomType.SHARED, 10);
        mockRoom.setId(1L);

        mockBooking = new Booking();
        mockBooking.setResponsiblePerson("John Doe");
        mockBooking.setDate(LocalDate.of(2025, 10, 25));
        mockBooking.setStartTime(LocalTime.of(10, 0));
        mockBooking.setEndTime(LocalTime.of(12, 0));
        mockBooking.setRoom(mockRoom);
    }

    @Test
    @DisplayName("Deve criar uma reserva com sucesso quando não houver conflito")
    void createBooking_Success() {
        // Configura o comportamento esperado dos Mocks
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(bookingRepository.existsConflictingBooking(
                eq(1L), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(false); // Diz que NÃO tem conflito

        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // Executa o método real
        Booking savedBooking = bookingService.createBooking(mockBooking);

        // Valida se o resultado é o esperado
        assertNotNull(savedBooking);
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class)); // Garante que salvou
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reservar com conflito de horário")
    void createBooking_WithConflict_ShouldThrowException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // Simula que JÁ EXISTE uma reserva no banco para esse horário
        when(bookingRepository.existsConflictingBooking(
                eq(1L), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(true);

        // Valida se a exceção correta foi lançada e impede o salvamento
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(mockBooking);
        });

        assertEquals("Já existe uma reserva confirmada para esta sala neste horário.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class)); // Garante que NÃO salvou
    }

    @Test
    @DisplayName("Deve lançar exceção quando o horário de início for depois do horário de fim")
    void createBooking_InvalidTimes_ShouldThrowException() {
        // Altera o horário para ser inválido (início 14h, fim 10h)
        mockBooking.setStartTime(LocalTime.of(14, 0));
        mockBooking.setEndTime(LocalTime.of(10, 0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(mockBooking);
        });

        assertEquals("O horário de início deve ser anterior ao horário de término.", exception.getMessage());
        // Não precisa nem bater no banco, já deve falhar antes
        verify(roomRepository, never()).findById(anyLong());
    }
}