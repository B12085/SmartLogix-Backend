package com.smartlogix.envios.service;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.model.EstadoEnvio;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EnvioService {

    Envio registrarEnvio(@NonNull Envio envio);

    List<Envio> listarTodos();

    List<Envio> listarDisponibles();

    Envio cambiarEstado(@NonNull String id, @NonNull EstadoEnvio estado);
}

