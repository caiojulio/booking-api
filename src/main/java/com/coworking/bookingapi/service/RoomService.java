package com.coworking.bookingapi.service;

import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.bookingapi.dto.RoomRequestDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
// Define que, por padrão, todos os métodos desta classe são apenas para leitura.
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Cadastra uma nova sala no sistema.
     */

    @Transactional
    public Room createRoom(RoomRequestDTO request) {
        Room room = new Room(request.name(), request.type(), request.capacity());
        return roomRepository.save(room);
    }

    /**
     * Lista todas as salas cadastradas.
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Consulta as salas disponíveis para uma data e período específicos.
     */
    public List<Room> getAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Regra de negócio simples: O horário de início deve ser anterior ao de término
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }

        return roomRepository.findAvailableRooms(date, startTime, endTime);
    }

    /**
     * Busca uma sala específica pelo ID.
     */
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }
}