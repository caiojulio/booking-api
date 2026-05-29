package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.BookingRequestDTO;
import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Endpoint para criar uma reserva.
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid BookingRequestDTO request) {
        Booking booking = new Booking();
        booking.setResponsiblePerson(request.responsiblePerson());
        booking.setDate(request.date());
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());

        // Criamos uma referência da sala apenas com o ID para passar ao Service
        Room roomReference = new Room();
        roomReference.setId(request.roomId());
        booking.setRoom(roomReference);

        Booking savedBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    /**
     * Endpoint para cancelar uma reserva existente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Booking cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    /**
     * Endpoint para consultar a agenda do dia.
     * Exemplo de uso: /api/bookings/agenda?date=2023-10-25
     */
    @GetMapping("/agenda")
    public ResponseEntity<List<Booking>> getDailyAgenda(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getDailyAgenda(date));
    }
}