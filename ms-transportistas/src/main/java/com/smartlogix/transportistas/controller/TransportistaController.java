package com.smartlogix.transportistas.controller;

import com.smartlogix.transportistas.model.Transportista;
import com.smartlogix.transportistas.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    @PostMapping("")
    public ResponseEntity<Transportista> registrarTransportista(@Valid @NonNull @RequestBody Transportista transportista) {
        try {
            Transportista transportistaRegistrado = transportistaService.registrarTransportista(transportista);
            return new ResponseEntity<>(transportistaRegistrado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Transportista>> listarTodos() {
        List<Transportista> transportistas = transportistaService.listarTodos();
        return new ResponseEntity<>(transportistas, HttpStatus.OK);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Transportista>> listarDisponibles() {
        List<Transportista> transportistas = transportistaService.listarDisponibles();
        return new ResponseEntity<>(transportistas, HttpStatus.OK);
    }

    @GetMapping("/buscar-por-id")
    public ResponseEntity<Transportista> buscarPorId(@NonNull @RequestParam String id) {
        try {
            Transportista transportista = transportistaService.buscarPorId(id);
            return new ResponseEntity<>(transportista, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/buscar-por-patente")
    public ResponseEntity<Transportista> buscarPorPatente(@NonNull @RequestParam String patente) {
        try {
            Transportista transportista = transportistaService.buscarPorPatente(patente);
            return new ResponseEntity<>(transportista, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Transportista> cambiarDisponibilidad(
            @NonNull @PathVariable String id,
            @NonNull @RequestParam Boolean disponible) {
        try {
            Transportista transportistaActualizado = transportistaService.cambiarDisponibilidad(id, disponible);
            return new ResponseEntity<>(transportistaActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> actualizarTransportista(
            @NonNull @PathVariable String id,
            @Valid @NonNull @RequestBody Transportista transportista) {
        try {
            Transportista transportistaActualizado = transportistaService.actualizarTransportista(id, transportista);
            return new ResponseEntity<>(transportistaActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransportista(@NonNull @PathVariable String id) {
        try {
            transportistaService.eliminarTransportista(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

