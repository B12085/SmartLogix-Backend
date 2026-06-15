package com.smartlogix.transportistas.controller;

import com.smartlogix.transportistas.model.Transportista;
import com.smartlogix.transportistas.service.TransportistaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransportistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransportistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransportistaService transportistaService;

    @Test
    void registrarTransportista_retorna201() throws Exception {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaService.registrarTransportista(ArgumentMatchers.<Transportista>any())).thenReturn(transportista);

        mockMvc.perform(
                        post("/api/transportistas")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("""
                                        {
                                          "nombre": "Juan Pérez",
                                          "patente": "ABC-1234",
                                          "telefono": "1234567890",
                                          "disponible": true
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.patente").value("ABC-1234"))
                .andExpect(jsonPath("$.id").value(transportista.getId()));
    }

    @Test
    void listarDisponibles_retorna200() throws Exception {
        Transportista t1 = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        t1.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista t2 = new Transportista("Carlos López", "DEF-5678", "0987654321", true);
        t2.setId("550e8400-e29b-41d4-a716-446655440001");

        when(transportistaService.listarDisponibles()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(
                        get("/api/transportistas/disponibles")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombre").value("Carlos López"))
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$[1].disponible").value(true));
    }

    @Test
    void cambiarDisponibilidad_aFalso_retorna200() throws Exception {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", false);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaService.cambiarDisponibilidad(eq(transportista.getId()), eq(false))).thenReturn(transportista);

        mockMvc.perform(
                        put("/api/transportistas/{id}/disponibilidad", transportista.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("disponible", "false")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false))
                .andExpect(jsonPath("$.id").value(transportista.getId()));
    }

    @Test
    void listarTodos_retorna200() throws Exception {
        Transportista t1 = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        t1.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista t2 = new Transportista("Carlos López", "DEF-5678", "0987654321", false);
        t2.setId("550e8400-e29b-41d4-a716-446655440001");

        when(transportistaService.listarTodos()).thenReturn(Arrays.asList(t1, t2));

        mockMvc.perform(
                        get("/api/transportistas/listar")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombre").value("Carlos López"));
    }

    @Test
    void buscarPorPatente_retorna200() throws Exception {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaService.buscarPorPatente("ABC-1234")).thenReturn(transportista);

        mockMvc.perform(
                        get("/api/transportistas/buscar-por-patente")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("patente", "ABC-1234")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.patente").value("ABC-1234"));
    }

    @Test
    void actualizarTransportista_retorna200() throws Exception {
        Transportista transportista = new Transportista("Juan Carlos Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaService.actualizarTransportista(eq(transportista.getId()), ArgumentMatchers.<Transportista>any()))
                .thenReturn(transportista);

        mockMvc.perform(
                        put("/api/transportistas/{id}", transportista.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("""
                                        {
                                          "nombre": "Juan Carlos Pérez",
                                          "patente": "ABC-1234",
                                          "telefono": "1234567890",
                                          "disponible": true
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos Pérez"))
                .andExpect(jsonPath("$.id").value(transportista.getId()));
    }
}

