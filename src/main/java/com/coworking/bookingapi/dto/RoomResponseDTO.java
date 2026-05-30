package com.coworking.bookingapi.dto;

import com.coworking.bookingapi.model.Room;

/**
 * DTO responsável por formatar a saída de dados de uma Sala,
 * protegendo a Entidade JPA de ser exposta diretamente na API.
 */
public record RoomResponseDTO(
        Long id,
        String name,
        String type, // Convertemos o Enum para String para simplificar o consumo no Frontend
        Integer capacity
) {
    /**
     * Mapeia uma Entidade JPA Room para o RoomResponseDTO.
     */
    public static RoomResponseDTO fromEntity(Room room) {
        return new RoomResponseDTO(
                room.getId(),
                room.getName(),
                room.getType().name(),
                room.getCapacity()
        );
    }
}