package com.smartlogix.transportistas.service;

import com.smartlogix.transportistas.model.Transportista;
import com.smartlogix.transportistas.repository.TransportistaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransportistaServiceImpl implements TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public TransportistaServiceImpl(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    @Override
    public Transportista registrarTransportista(@NonNull Transportista transportista) {
        if (transportista.getNombre() == null || transportista.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del transportista no puede estar vacío");
        }

        if (transportista.getPatente() == null || transportista.getPatente().trim().isEmpty()) {
            throw new IllegalArgumentException("La patente no puede estar vacía");
        }

        if (transportista.getTelefono() == null || transportista.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }

        // Validar que la patente no exista
        Optional<Transportista> existente = transportistaRepository.findByPatente(transportista.getPatente());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un transportista registrado con esta patente");
        }

        return transportistaRepository.save(transportista);
    }

    @Override
    public List<Transportista> listarTodos() {
        return transportistaRepository.findAll();
    }

    @Override
    public List<Transportista> listarDisponibles() {
        return transportistaRepository.findByDisponibleTrue();
    }

    @Override
    public Transportista buscarPorId(@NonNull String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de transportista inválido");
        }

        return Objects.requireNonNull(transportistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado con ID: " + id)));
    }

    @Override
    public Transportista buscarPorPatente(@NonNull String patente) {
        if (patente == null || patente.trim().isEmpty()) {
            throw new IllegalArgumentException("Patente inválida");
        }

        return Objects.requireNonNull(transportistaRepository.findByPatente(patente)
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado con patente: " + patente)));
    }

    @Override
    public Transportista cambiarDisponibilidad(@NonNull String id, @NonNull Boolean disponible) {
        Transportista transportista = buscarPorId(id);
        transportista.setDisponible(disponible);
        return Objects.requireNonNull(transportistaRepository.save(transportista));
    }

    @Override
    public Transportista actualizarTransportista(@NonNull String id, @NonNull Transportista transportistaActualizado) {
        Transportista transportista = buscarPorId(id);

        if (transportistaActualizado.getNombre() != null && !transportistaActualizado.getNombre().isEmpty()) {
            transportista.setNombre(transportistaActualizado.getNombre());
        }

        if (transportistaActualizado.getPatente() != null && !transportistaActualizado.getPatente().isEmpty()) {
            // Validar que la nueva patente no exista en otro transportista
            Optional<Transportista> existente = transportistaRepository.findByPatente(transportistaActualizado.getPatente());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro transportista con esta patente");
            }
            transportista.setPatente(transportistaActualizado.getPatente());
        }

        if (transportistaActualizado.getTelefono() != null && !transportistaActualizado.getTelefono().isEmpty()) {
            transportista.setTelefono(transportistaActualizado.getTelefono());
        }

        if (transportistaActualizado.getDisponible() != null) {
            transportista.setDisponible(transportistaActualizado.getDisponible());
        }

        return Objects.requireNonNull(transportistaRepository.save(transportista));
    }

    @Override
    public void eliminarTransportista(@NonNull String id) {
        Transportista transportista = buscarPorId(id);
        transportistaRepository.delete(transportista);
    }
}

