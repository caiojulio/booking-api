package com.coworking.bookingapi.repository;

import com.coworking.bookingapi.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Retorna todas as salas que NÃO possuem reservas confirmadas
     * sobrepostas ao período solicitado.
     */
    @Query("""
        SELECT r FROM Room r 
        WHERE r.id NOT IN (
            SELECT b.room.id FROM Booking b 
            WHERE b.date = :date 
            AND b.status = 'CONFIRMED' 
            AND (b.startTime < :endTime AND b.endTime > :startTime)
        )
    """)
    List<Room> findAvailableRooms(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}