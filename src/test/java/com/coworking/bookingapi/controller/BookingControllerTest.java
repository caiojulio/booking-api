package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.model.Booking;
import com.coworking.bookingapi.model.BookingStatus;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.model.RoomType;
import com.coworking.bookingapi.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    private Booking mockBooking;

    @BeforeEach
    void setUp() {
        Room mockRoom = new Room("Sala Focus", RoomType.INDIVIDUAL, 1);
        ReflectionTestUtils.setField(mockRoom, "id", 1L);

        mockBooking = new Booking(
                "Maria Silva",
                LocalDate.of(2025, 12, 10),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                mockRoom
        );
        ReflectionTestUtils.setField(mockBooking, "id", 1L);
        // Por padrão o status ao criar é CONFIRMED
    }

    @Test
    @DisplayName("Deve retornar 200 OK e dados paginados ao listar todas as reservas")
    void shouldReturnPagedBookingsWithStatus200() throws Exception {
        // Arrange
        Page<Booking> pagedResponse = new PageImpl<>(List.of(mockBooking), PageRequest.of(0, 10), 1);
        Mockito.when(bookingService.getAllBookings(any())).thenReturn(pagedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/bookings")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validações do JSON de resposta garantindo que o BookingResponseDTO está correto
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].responsiblePerson").value("Maria Silva"))
                .andExpect(jsonPath("$.content[0].roomName").value("Sala Focus"))
                .andExpect(jsonPath("$.content[0].status").value("CONFIRMED"))

                // Validações dos metadados de Paginação
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }
}