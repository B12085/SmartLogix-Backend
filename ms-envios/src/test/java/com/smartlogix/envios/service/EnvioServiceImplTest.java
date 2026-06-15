package com.smartlogix.envios.service;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.model.EstadoEnvio;
import com.smartlogix.envios.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioServiceImpl envioService;

    @Test
    void registrarEnvio_exitoso() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);

        when(envioRepository.save(ArgumentMatchers.<Envio>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.registrarEnvio(envio);

        assertEquals(1L, resultado.getPedidoId());
        assertEquals("Calle 123", resultado.getDireccionEntrega());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstado());
    }

    @Test
    void registrarEnvio_pedidoIdNull_lanzaExcepcion() {
        Envio envio = new Envio(null, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_pedidoIdMenorOIgualACero_lanzaExcepcion() {
        Envio envio = new Envio(0L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_transportistaIdNull_lanzaExcepcion() {
        Envio envio = new Envio(1L, null, "Calle 123", EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_transportistaIdVacio_lanzaExcepcion() {
        Envio envio = new Envio(1L, "", "Calle 123", EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_direccionEntregaNull_lanzaExcepcion() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", null, EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_direccionEntregaVacia_lanzaExcepcion() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "", EstadoEnvio.PENDIENTE);

        assertThrows(IllegalArgumentException.class, () -> envioService.registrarEnvio(envio));
    }

    @Test
    void registrarEnvio_estadoNull_setearPendiente() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", null);

        when(envioRepository.save(ArgumentMatchers.<Envio>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.registrarEnvio(envio);

        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstado());
    }

    @Test
    void cambiarEstado_exitoso() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        envio.setId("envio-001");

        when(envioRepository.findById(envio.getId())).thenReturn(Optional.of(envio));
        when(envioRepository.save(ArgumentMatchers.<Envio>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.cambiarEstado(envio.getId(), EstadoEnvio.EN_RUTA);

        assertEquals(EstadoEnvio.EN_RUTA, resultado.getEstado());
    }

    @Test
    void cambiarEstado_aEntregado() {
        Envio envio = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.EN_RUTA);
        envio.setId("envio-001");

        when(envioRepository.findById(envio.getId())).thenReturn(Optional.of(envio));
        when(envioRepository.save(ArgumentMatchers.<Envio>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.cambiarEstado(envio.getId(), EstadoEnvio.ENTREGADO);

        assertEquals(EstadoEnvio.ENTREGADO, resultado.getEstado());
    }

    @Test
    void cambiarEstado_envioNoExiste_lanzaExcepcion() {
        when(envioRepository.findById("no-existe")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> envioService.cambiarEstado("no-existe", EstadoEnvio.ENTREGADO));
    }

    @Test
    void listarTodos_retornaListaCompleta() {
        Envio envio1 = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        Envio envio2 = new Envio(2L, "550e8400-e29b-41d4-a716-446655440001", "Calle 456", EstadoEnvio.EN_RUTA);

        when(envioRepository.findAll()).thenReturn(java.util.List.of(envio1, envio2));

        java.util.List<Envio> resultado = envioService.listarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void listarDisponibles_retornaEnviosNoEntregados() {
        Envio envio1 = new Envio(1L, "550e8400-e29b-41d4-a716-446655440000", "Calle 123", EstadoEnvio.PENDIENTE);
        Envio envio2 = new Envio(2L, "550e8400-e29b-41d4-a716-446655440001", "Calle 456", EstadoEnvio.EN_RUTA);

        when(envioRepository.findByEstadoNot(EstadoEnvio.ENTREGADO)).thenReturn(java.util.List.of(envio1, envio2));

        java.util.List<Envio> resultado = envioService.listarDisponibles();

        assertEquals(2, resultado.size());
    }
}

