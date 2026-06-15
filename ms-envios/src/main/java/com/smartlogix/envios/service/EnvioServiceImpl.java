package com.smartlogix.envios.service;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.model.EstadoEnvio;
import com.smartlogix.envios.repository.EnvioRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioServiceImpl(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public Envio registrarEnvio(@NonNull Envio envio) {
        if (envio.getPedidoId() == null || envio.getPedidoId() <= 0) {
            throw new IllegalArgumentException("El pedidoId debe ser un número válido");
        }
        if (envio.getTransportistaId() == null || envio.getTransportistaId().trim().isEmpty()) {
            throw new IllegalArgumentException("El transportistaId es requerido");
        }
        if (envio.getDireccionEntrega() == null || envio.getDireccionEntrega().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de entrega es requerida");
        }
        if (envio.getEstado() == null) {
            envio.setEstado(EstadoEnvio.PENDIENTE);
        }
        return envioRepository.save(envio);
    }

    @Override
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    @Override
    public List<Envio> listarDisponibles() {
        return envioRepository.findByEstadoNot(EstadoEnvio.ENTREGADO);
    }

    @Override
    public Envio cambiarEstado(@NonNull String id, @NonNull EstadoEnvio estado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Envio no encontrado con ID: " + id));
        envio.setEstado(estado);
        return Objects.requireNonNull(envioRepository.save(envio));
    }
}

