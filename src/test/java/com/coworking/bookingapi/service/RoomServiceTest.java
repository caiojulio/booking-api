package com.coworking.bookingapi.service;

import com.coworking.bookingapi.dto.RoomRequestDTO;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.model.RoomType;
import com.coworking.bookingapi.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a camada de serviço de salas.
 *
 * (Nota: As descrições dos testes utilizam o idioma Português para facilitar
 * a leitura dos relatórios, conforme decisão arquitetural do projeto).
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room mockRoom;
    private RoomRequestDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        // Instanciação segura através do construtor de domínio rico
        mockRoom = new Room("Auditorio Principal", RoomType.AUDITORIUM, 50);

        // Injeção de ID contornando o encapsulamento (exclusivo para testes)
        ReflectionTestUtils.setField(mockRoom, "id", 1L);

        validRequestDTO = new RoomRequestDTO("Auditorio Principal", RoomType.AUDITORIUM, 50);
    }

    @Test
    @DisplayName("Deve guardar e retornar uma nova sala com sucesso")
    void createRoom_Success() {
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);

        Room savedRoom = roomService.createRoom(validRequestDTO);

        assertNotNull(savedRoom);
        assertEquals("Auditorio Principal", savedRoom.getName());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar sala com nome em branco")
    void createRoom_BlankName_ShouldThrowException() {
        RoomRequestDTO invalidRequest = new RoomRequestDTO("   ", RoomType.SHARED, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(invalidRequest);
        });

        assertEquals("O nome da sala é obrigatório.", exception.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar sala com capacidade zero ou negativa")
    void createRoom_InvalidCapacity_ShouldThrowException() {
        RoomRequestDTO invalidRequest = new RoomRequestDTO("Sala B", RoomType.INDIVIDUAL, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(invalidRequest);
        });

        assertEquals("A capacidade da sala deve ser maior que zero.", exception.getMessage());
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista com todas as salas")
    void getAllRooms_Success() {
        Room room2 = new Room("Sala de Reunioes", RoomType.SHARED, 10);
        ReflectionTestUtils.setField(room2, "id", 2L);

        when(roomRepository.findAll()).thenReturn(Arrays.asList(mockRoom, room2));

        List<Room> rooms = roomService.getAllRooms();

        assertEquals(2, rooms.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma sala quando o ID existir")
    void getRoomById_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        Optional<Room> foundRoom = roomService.getRoomById(1L);

        assertTrue(foundRoom.isPresent());
        assertEquals(mockRoom.getName(), foundRoom.get().getName());
    }
}