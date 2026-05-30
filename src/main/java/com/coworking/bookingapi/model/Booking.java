package com.coworking.bookingapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "responsible_person", nullable = false, length = 100)
    private String responsiblePerson;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Construtor que garante que nenhuma reserva nasça em um estado inválido ou sem os dados obrigatórios.
     */
    public Booking(String responsiblePerson, LocalDate date, LocalTime startTime, LocalTime endTime, Room room) {
        validateTimes(startTime, endTime);

        this.responsiblePerson = responsiblePerson;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Comportamento de Domínio (Business Behavior):
     * Centraliza a lógica de cancelamento dentro da própria entidade.
     */
    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Esta reserva já encontra-se cancelada.");
        }
        this.status = BookingStatus.CANCELLED;
    }

    /**
     * Abstração de Validação Interna.
     */
    private void validateTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Os horários de início e término são obrigatórios.");
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }
    }
}