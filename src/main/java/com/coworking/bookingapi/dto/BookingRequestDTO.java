package com.coworking.bookingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequestDTO(
        @NotBlank(message = "O nome do responsável é obrigatório")
        String responsiblePerson,

        @NotNull(message = "A data da reserva é obrigatória")
        LocalDate date,

        @NotNull(message = "O horário de início é obrigatório")
        LocalTime startTime,

        @NotNull(message = "O horário de término é obrigatório")
        LocalTime endTime,

        @NotNull(message = "O ID da sala é obrigatório")
        Long roomId
) {}