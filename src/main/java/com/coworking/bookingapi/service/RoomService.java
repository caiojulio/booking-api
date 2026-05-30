package com.coworking.bookingapi.service;

import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.coworking.bookingapi.dto.RoomRequestDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Cadastra uma nova sala no sistema.
     */
    public Room createRoom(RoomRequestDTO request) {
        // A conversão (DTO -> Entidade) agora acontece no lugar certo: na camada de negócio.
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
     * Busca uma sala específica pelo ID.
     */
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }
}