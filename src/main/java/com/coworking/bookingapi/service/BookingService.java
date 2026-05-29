package com.coworking.bookingapi.service;

import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.model.BookingStatus;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.repository.BookingRepository;
import com.coworking.bookingapi.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    // Injeção de dependência via construtor com múltiplas dependências
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Realiza uma nova reserva validando conflitos de horário.
     */
    @Transactional
    public Booking createBooking(Booking booking) {
        // Horário de início deve ser antes do fim
        if (booking.getStartTime().isAfter(booking.getEndTime()) || booking.getStartTime().equals(booking.getEndTime())) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }

        // Verifica se a sala existe
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada com o ID informado."));

        booking.setRoom(room);

        // Validação do conflito de horários
        boolean hasConflict = bookingRepository.existsConflictingBooking(
                room.getId(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (hasConflict) {
            throw new IllegalStateException("Já existe uma reserva confirmada para esta sala neste horário.");
        }

        // Define o status inicial e salva
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