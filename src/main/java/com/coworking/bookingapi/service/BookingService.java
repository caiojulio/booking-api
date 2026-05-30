package com.coworking.bookingapi.service;

import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.model.BookingStatus;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.repository.BookingRepository;
import com.coworking.bookingapi.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.bookingapi.dto.BookingRequestDTO;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    /**
     * Realiza uma nova reserva validando conflitos de horário.
     */
    @Transactional
    public Booking createBooking(BookingRequestDTO request) {
        if (request.startTime().isAfter(request.endTime()) || request.startTime().equals(request.endTime())) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada com o ID informado."));

        boolean hasConflict = bookingRepository.existsConflictingBooking(
                room.getId(),
                request.date(),
                request.startTime(),
                request.endTime()
        );

        if (hasConflict) {
            throw new IllegalStateException("Já existe uma reserva confirmada para esta sala neste horário.");
        }

        Booking booking = new Booking();
        booking.setResponsiblePerson(request.responsiblePerson());
        booking.setDate(request.date());
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());
        booking.setRoom(room);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    /**
     * Cancela uma reserva existente.
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Esta reserva já está cancelada.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    /**
     * Consulta a agenda diária (apenas reservas ativas).
     */
    public List<Booking> getDailyAgenda(LocalDate date) {
        return bookingRepository.findByDateAndStatusOrderByStartTimeAsc(date, BookingStatus.CONFIRMED);
    }
}