package com.smartlogix.envios.controller;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.model.EstadoEnvio;
import com.smartlogix.envios.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnvioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(envioController).build();
    }

    @Test
    void registrarEnvio_retorna201() throws Exception {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        envio.setId("envio-001");

        when(envioService.registrarEnvio(ArgumentMatchers.<Envio>any())).thenReturn(envio);

        mockMvc.perform(
                        post("/api/envios")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("""
                                        {
                                          "pedido_id": 1,
                                          "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
                                          "direccion_entrega": "Calle 123",
                                          "estado": "PENDIENTE"
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedido_id").value(1))
                .andExpect(jsonPath("$.transportista_id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.direccion_entrega").value("Calle 123"));
    }

    @Test
    void listarEnvios_retorna200() throws Exception {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        envio.setId("envio-001");

        when(envioService.listarTodos()).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/envios/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("envio-001"))
                .andExpect(jsonPath("$[0].pedido_id").value(1));
    }

    @Test
    void listarDisponibles_retorna200() throws Exception {
        Envio envio1 = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        envio1.setId("envio-001");
        Envio envio2 = new Envio(2L, "550e8400-e29b-41d4-a716-446655440001", "Calle 456", EstadoEnvio.EN_RUTA);
        envio2.setId("envio-002");

        when(envioService.listarDisponibles()).thenReturn(List.of(envio1, envio2));

        mockMvc.perform(get("/api/envios/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("envio-001"))
                .andExpect(jsonPath("$[1].id").value("envio-002"));
    }

    @Test
    void cambiarEstado_aEnRuta_retorna200() throws Exception {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.EN_RUTA);
        envio.setId("envio-001");

        when(envioService.cambiarEstado(eq(envio.getId()), eq(EstadoEnvio.EN_RUTA))).thenReturn(envio);

        mockMvc.perform(
                        put("/api/envios/{id}/estado", envio.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("estado", "EN_RUTA")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_RUTA"))
                .andExpect(jsonPath("$.id").value(envio.getId()));
    }

    @Test
    void cambiarEstado_aEntregado_retorna200() throws Exception {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.ENTREGADO);
        envio.setId("envio-001");

        when(envioService.cambiarEstado(eq(envio.getId()), eq(EstadoEnvio.ENTREGADO))).thenReturn(envio);

        mockMvc.perform(
                        put("/api/envios/{id}/estado", envio.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("estado", "ENTREGADO")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }
}

