package com.coworking.bookingapi.dto;

import com.coworking.bookingapi.model.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório")
        String name,

        @NotNull(message = "O tipo da sala é obrigatório")
        RoomType type,

        @NotNull(message = "A capacidade é obrigatória")
        @Positive(message = "A capacidade deve ser maior que zero")
        Integer capacity
) {}