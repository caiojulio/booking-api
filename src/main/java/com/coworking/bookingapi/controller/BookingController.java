package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.BookingRequestDTO;
import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas, cancelamentos e consulta de agenda")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Criar uma nova reserva", description = "Registra uma reserva validando a disponibilidade de horário da sala para evitar conflitos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou conflito de regras de negócio (ex: horário indisponível)")
    })
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid BookingRequestDTO request) {
        Booking savedBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @Operation(summary = "Cancelar uma reserva", description = "Altera o status de uma reserva existente para CANCELLED (Cancelada).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Reserva já encontra-se cancelada ou parâmetros inválidos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Booking cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @Operation(summary = "Consultar agenda do dia", description = "Retorna uma lista cronológica de todas as reservas confirmadas para uma data específica.")
    @ApiResponse(responseCode = "200", description = "Agenda retornada com sucesso")
    @GetMapping("/agenda")
    public ResponseEntity<List<Booking>> getDailyAgenda(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getDailyAgenda(date));
    }
}