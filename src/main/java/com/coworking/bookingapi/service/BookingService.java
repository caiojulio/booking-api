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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada com o ID informado."));

        validateConflict(room.getId(), request);

        // A entidade assume o controle: Valida horários internamente e nasce num estado válido
        Booking booking = new Booking(
                request.responsiblePerson(),
                request.date(),
                request.startTime(),
                request.endTime(),
                room
        );

        return bookingRepository.save(booking);
    }

    /**
     * Cancela uma reserva existente.
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada com o ID informado."));

        booking.cancel();

        return bookingRepository.save(booking);
    }

    /**
     * Consulta a agenda diária (apenas reservas ativas).
     */
    public List<Booking> getDailyAgenda(LocalDate date) {
        return bookingRepository.findByDateAndStatusOrderByStartTimeAsc(date, BookingStatus.CONFIRMED);
    }

    /**
     * Consulta todas as reservas
     */
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    /**
     * Extração de metodo privado para manter o metodo principal focado (responsabilidade unica).
     */
    private void validateConflict(Long roomId, BookingRequestDTO request) {
        boolean hasConflict = bookingRepository.existsConflictingBooking(
                roomId,
                request.date(),
                request.startTime(),
                request.endTime()
        );

        if (hasConflict) {
            throw new IllegalStateException("Já existe uma reserva confirmada para esta sala neste horário.");
        }
    }
}