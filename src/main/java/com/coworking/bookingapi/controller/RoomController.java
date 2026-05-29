package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.RoomRequestDTO;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    // Injeção de dependência via construtor
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint para cadastrar uma nova sala.
     * O @Valid garante que as regras definidas no Record sejam respeitadas.
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody @Valid RoomRequestDTO request) {
        Room room = new Room(request.name(), request.type(), request.capacity());
        Room savedRoom = roomService.createRoom(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    /**
     * Endpoint para listar todas as salas.
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    /**
     * Endpoint para buscar uma sala específica pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}