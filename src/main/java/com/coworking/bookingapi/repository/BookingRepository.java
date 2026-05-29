package com.coworking.bookingapi.repository;

import com.coworking.bookingapi.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Verifica se existe alguma reserva confirmada para a mesma sala, na mesma data,
     * que se sobreponha aos horários solicitados.
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.date = :date " +
            "AND b.status = 'CONFIRMED' " +
            "AND (b.startTime < :endTime AND b.endTime > :startTime)")
    boolean existsConflictingBooking(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    /**
     * Consulta a agenda diária (todas as reservas confirmadas de um dia).
     */
    List<Booking> findByDateAndStatusOrderByStartTimeAsc(LocalDate date, com.coworking.bookingapi.model.BookingStatus status);
}