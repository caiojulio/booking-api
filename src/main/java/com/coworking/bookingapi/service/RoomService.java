package com.coworking.bookingapi.service;

import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    // Injeção de dependência via construtor
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Cadastra uma nova sala no sistema.
     */
    public Room createRoom(Room room) {
        // Como é um cadastro simples, delegamos direto para o repositório.
        // Futuramente, regras adicionais de validação de sala entrariam aqui.
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