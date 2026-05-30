package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.RoomRequestDTO;
import com.coworking.bookingapi.dto.RoomResponseDTO;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody @Valid RoomRequestDTO request) {
        Room savedRoom = roomService.createRoom(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedRoom.getId())
                .toUri();

        return ResponseEntity.created(location).body(RoomResponseDTO.fromEntity(savedRoom));
    }

    @Operation(summary = "Listar todas as salas", description = "Retorna uma lista contendo todas as salas registradas no coworking.")
    @ApiResponse(responseCode = "200", description = "Lista de salas retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        List<RoomResponseDTO> rooms = roomService.getAllRooms().stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Buscar sala por ID", description = "Retorna os detalhes completos de uma sala específica cadastrada no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sala encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada com o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(RoomResponseDTO::fromEntity) // Mapeia a Entidade contida no Optional para DTO
                .map(ResponseEntity::ok)          // Se estiver presente, encapsula no 200 OK
                .orElse(ResponseEntity.notFound().build()); // Se não, retorna 404
    }
}