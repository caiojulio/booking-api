package com.coworking.bookingapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomType type;

    @Column(nullable = false)
    private Integer capacity;

    // Relacionamento bidirecional mapeado pelo campo room na classe Booking.
    // Inicializado vazio para evitar NullPointerException.
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Construtor que exige que os dados mínimos e aplica as regras para uma sala existir.
     */
    public Room(String name, RoomType type, Integer capacity) {
        validateRoom(name, type, capacity);

        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    /**
     * Comportamento e atualizações controladas.
     * Se no futuro precisarmos atualizar os dados da sala, fazemos por métodos específicos.
     */
    public void updateDetails(String name, RoomType type, Integer capacity) {
        validateRoom(name, type, capacity);

        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    /**
     * Validações centralizadas
     */
    private void validateRoom(String name, RoomType type, Integer capacity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da sala é obrigatório.");
        }
        if (type == null) {
            throw new IllegalArgumentException("O tipo da sala é obrigatório.");
        }
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("A capacidade da sala deve ser maior que zero.");
        }
    }
}