package com.smartlogix.transportistas.service;

import com.smartlogix.transportistas.model.Transportista;
import org.springframework.lang.NonNull;
import java.util.List;

public interface TransportistaService {

    Transportista registrarTransportista(@NonNull Transportista transportista);

    List<Transportista> listarTodos();

    List<Transportista> listarDisponibles();

    Transportista buscarPorId(@NonNull String id);

    Transportista buscarPorPatente(@NonNull String patente);

    Transportista cambiarDisponibilidad(@NonNull String id, @NonNull Boolean disponible);

    Transportista actualizarTransportista(@NonNull String id, @NonNull Transportista transportista);

    void eliminarTransportista(@NonNull String id);
}

