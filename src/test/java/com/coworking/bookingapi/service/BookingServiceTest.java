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
    private BookingRequestDTO validRequest; // Adicionado para os testes

    @BeforeEach
    void setUp() {
        mockRoom = new Room("Sala A", RoomType.SHARED, 10);
        mockRoom.setId(1L);

        // Objeto de retorno esperado do banco
        mockBooking = new Booking();
        mockBooking.setResponsiblePerson("John Doe");
        mockBooking.setDate(LocalDate.of(2025, 10, 25));
        mockBooking.setStartTime(LocalTime.of(10, 0));
        mockBooking.setEndTime(LocalTime.of(12, 0));
        mockBooking.setRoom(mockRoom);
        mockBooking.setStatus(BookingStatus.CONFIRMED);

        // Novo: O DTO que simula a requisição do usuário
        // ATENÇÃO: Verifique se a ordem dos parâmetros aqui bate com o seu record BookingRequestDTO
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

        // Agora passamos o validRequest (DTO) em vez do mockBooking
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
            // Passamos o validRequest (DTO)
            bookingService.createBooking(validRequest);
        });

        assertEquals("Já existe uma reserva confirmada para esta sala neste horário.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o horário de início for depois do horário de fim")
    void createBooking_InvalidTimes_ShouldThrowException() {
        // Criamos um DTO específico com horários inválidos para este teste
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
        verify(roomRepository, never()).findById(anyLong());
    }
}