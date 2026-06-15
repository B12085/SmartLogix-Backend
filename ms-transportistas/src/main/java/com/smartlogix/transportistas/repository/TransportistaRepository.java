package com.smartlogix.transportistas.repository;

import com.smartlogix.transportistas.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

// Interfaz del repositorio para operaciones de base de datos con la entidad Transportista
@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, String> {

    // Método personalizado para buscar un transportista por su patente
    Optional<Transportista> findByPatente(String patente);

    // Método para listar todos los transportistas disponibles
    List<Transportista> findByDisponibleTrue();
}

