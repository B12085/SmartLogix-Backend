package com.smartlogix.transportistas.service;

import com.smartlogix.transportistas.model.Transportista;
import com.smartlogix.transportistas.repository.TransportistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TransportistaServiceImplTest {

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private TransportistaServiceImpl transportistaService;

    @Test
    void registrarTransportista_exitoso() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findByPatente(transportista.getPatente())).thenReturn(Optional.empty());
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenReturn(transportista);

        Transportista resultado = transportistaService.registrarTransportista(transportista);

        assertEquals(transportista.getId(), resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("ABC-1234", resultado.getPatente());
    }

    @Test
    void registrarTransportista_nombreVacio_lanzaExcepcion() {
        Transportista transportista = new Transportista("", "ABC-1234", "1234567890", true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_nombreNull_lanzaExcepcion() {
        Transportista transportista = new Transportista(null, "ABC-1234", "1234567890", true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_patenteVacia_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", "", "1234567890", true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_patenteNull_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", null, "1234567890", true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_telefonoVacio_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "", true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_telefonoNull_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", null, true);

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void registrarTransportista_patenteExistente_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        Transportista existente = new Transportista("Carlos López", "ABC-1234", "0987654321", true);

        when(transportistaRepository.findByPatente(transportista.getPatente())).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> transportistaService.registrarTransportista(transportista));
    }

    @Test
    void listarTodos_retornaListaCompleta() {
        Transportista t1 = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        t1.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista t2 = new Transportista("Carlos López", "DEF-5678", "0987654321", true);
        t2.setId("550e8400-e29b-41d4-a716-446655440001");

        when(transportistaRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Transportista> resultado = transportistaService.listarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void listarDisponibles_retornaLista() {
        Transportista t1 = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        t1.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista t2 = new Transportista("Carlos López", "DEF-5678", "0987654321", true);
        t2.setId("550e8400-e29b-41d4-a716-446655440001");

        when(transportistaRepository.findByDisponibleTrue()).thenReturn(Arrays.asList(t1, t2));

        List<Transportista> resultado = transportistaService.listarDisponibles();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(Transportista::getDisponible));
    }

    @Test
    void buscarPorId_exitoso() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));

        Transportista resultado = transportistaService.buscarPorId(transportista.getId());

        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("ABC-1234", resultado.getPatente());
    }

    @Test
    void buscarPorId_transportistaNoExiste_lanzaExcepcion() {
        when(transportistaRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transportistaService.buscarPorId("id-inexistente"));
    }

    @Test
    void buscarPorId_idVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> transportistaService.buscarPorId(""));
    }

    @Test
    void buscarPorPatente_exitoso() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findByPatente("ABC-1234")).thenReturn(Optional.of(transportista));

        Transportista resultado = transportistaService.buscarPorPatente("ABC-1234");

        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("ABC-1234", resultado.getPatente());
    }

    @Test
    void buscarPorPatente_patenteNoExiste_lanzaExcepcion() {
        when(transportistaRepository.findByPatente("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transportistaService.buscarPorPatente("NO-EXISTE"));
    }

    @Test
    void buscarPorPatente_patenteVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> transportistaService.buscarPorPatente(""));
    }

    @Test
    void cambiarDisponibilidad_aFalso() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.cambiarDisponibilidad(transportista.getId(), false);

        assertEquals(false, resultado.getDisponible());
    }

    @Test
    void cambiarDisponibilidad_aVerdadero() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", false);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.cambiarDisponibilidad(transportista.getId(), true);

        assertEquals(true, resultado.getDisponible());
    }

    @Test
    void actualizarTransportista_actualizaNombre() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista actualizado = new Transportista("Juan Carlos Pérez", "ABC-1234", "1234567890", true);

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.actualizarTransportista(transportista.getId(), actualizado);

        assertEquals("Juan Carlos Pérez", resultado.getNombre());
    }

    @Test
    void actualizarTransportista_actualizaPatente() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista actualizado = new Transportista("Juan Pérez", "XYZ-9999", "1234567890", true);

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.findByPatente("XYZ-9999")).thenReturn(Optional.empty());
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.actualizarTransportista(transportista.getId(), actualizado);

        assertEquals("XYZ-9999", resultado.getPatente());
    }

    @Test
    void actualizarTransportista_patenteExistenteEnOtro_lanzaExcepcion() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista otro = new Transportista("Carlos López", "XYZ-9999", "0987654321", true);
        otro.setId("550e8400-e29b-41d4-a716-446655440001");
        Transportista actualizado = new Transportista("Juan Pérez", "XYZ-9999", "1234567890", true);

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.findByPatente("XYZ-9999")).thenReturn(Optional.of(otro));

        assertThrows(IllegalArgumentException.class, () -> 
            transportistaService.actualizarTransportista(transportista.getId(), actualizado));
    }

    @Test
    void actualizarTransportista_actualizaTelefono() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista actualizado = new Transportista("Juan Pérez", "ABC-1234", "9999999999", true);

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.actualizarTransportista(transportista.getId(), actualizado);

        assertEquals("9999999999", resultado.getTelefono());
    }

    @Test
    void actualizarTransportista_actualizaDisponibilidad() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");
        Transportista actualizado = new Transportista("Juan Pérez", "ABC-1234", "1234567890", false);

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));
        when(transportistaRepository.save(ArgumentMatchers.<Transportista>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transportista resultado = transportistaService.actualizarTransportista(transportista.getId(), actualizado);

        assertEquals(false, resultado.getDisponible());
    }

    @Test
    void eliminarTransportista_exitoso() {
        Transportista transportista = new Transportista("Juan Pérez", "ABC-1234", "1234567890", true);
        transportista.setId("550e8400-e29b-41d4-a716-446655440000");

        when(transportistaRepository.findById(transportista.getId())).thenReturn(Optional.of(transportista));

        transportistaService.eliminarTransportista(transportista.getId());

        verify(transportistaRepository, times(1)).delete(transportista);
    }

    @Test
    void eliminarTransportista_transportistaNoExiste_lanzaExcepcion() {
        when(transportistaRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transportistaService.eliminarTransportista("id-inexistente"));
    }
}

