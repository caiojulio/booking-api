package com.coworking.bookingapi.controller;

import com.coworking.bookingapi.dto.RoomRequestDTO;
import com.coworking.bookingapi.model.Room;
import com.coworking.bookingapi.model.RoomType;
import com.coworking.bookingapi.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da camada Web para as Salas utilizando MockMvc.
 * (Nota: As descrições dos testes utilizam o idioma Português para facilitar
 * a leitura dos relatórios, conforme decisão arquitetural do projeto).
 */
@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("Deve retornar 201 Created e o cabeçalho Location ao criar uma sala com dados válidos")
    void createRoom_WithValidData_ReturnsCreated() throws Exception {
        RoomRequestDTO request = new RoomRequestDTO("Sala A", RoomType.INDIVIDUAL, 1);

        // Uso do construtor rico e injeção de ID via Reflection para contornar o encapsulamento
        Room mockRoom = new Room("Sala A", RoomType.INDIVIDUAL, 1);
        ReflectionTestUtils.setField(mockRoom, "id", 1L);

        Mockito.when(roomService.createRoom(any(RoomRequestDTO.class))).thenReturn(mockRoom);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location")) // Valida nossa boa prática RESTful
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/rooms/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sala A"))
                .andExpect(jsonPath("$.type").value("INDIVIDUAL")); // Valida conversão do Enum pelo DTO
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar uma sala sem nome")
    void createRoom_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Envia um nome vazio (violando a anotação @NotBlank do DTO)
        RoomRequestDTO request = new RoomRequestDTO("", RoomType.INDIVIDUAL, 1);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("O nome da sala é obrigatório"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de salas formatada pelo DTO")
    void getAllRooms_ReturnsOk() throws Exception {
        Room room1 = new Room("Sala A", RoomType.INDIVIDUAL, 1);
        ReflectionTestUtils.setField(room1, "id", 1L);

        Room room2 = new Room("Sala B", RoomType.SHARED, 5);
        ReflectionTestUtils.setField(room2, "id", 2L);

        Mockito.when(roomService.getAllRooms()).thenReturn(Arrays.asList(room1, room2));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sala A"))
                .andExpect(jsonPath("$[0].type").value("INDIVIDUAL")) // Verifica a serialização do ResponseDTO
                .andExpect(jsonPath("$[1].name").value("Sala B"));
    }
}