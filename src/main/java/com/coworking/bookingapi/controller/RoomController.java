package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.RoomRequestDTO;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Endpoints para gerenciamento e cadastro das salas do coworking")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Cadastrar uma nova sala", description = "Cria uma nova sala no sistema para ficar disponível para reservas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sala criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados na requisição")
    })
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody @Valid RoomRequestDTO request) {
        Room savedRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @Operation(summary = "Listar todas as salas", description = "Retorna uma lista contendo todas as salas registradas no coworking.")
    @ApiResponse(responseCode = "200", description = "Lista de salas retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @Operation(summary = "Buscar sala por ID", description = "Retorna os detalhes completos de uma sala específica cadastrada no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada com o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}