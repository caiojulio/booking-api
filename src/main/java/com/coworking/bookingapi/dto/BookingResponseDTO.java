package com.coworking.bookingapi.dto;

import com.coworking.bookingapi.model.Booking;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO responsável por formatar a saída de dados de uma Reserva,
 * protegendo a Entidade JPA de ser exposta diretamente na API.
 */
public record BookingResponseDTO(
        Long id,
        String responsiblePerson,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String roomName // Trazemos apenas o nome da sala, evitando expor o objeto Room inteiro
) {
    // Factory method prático para conversão
    public static BookingResponseDTO fromEntity(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getResponsiblePerson(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name(),
                booking.getRoom().getName()
        );
    }
}