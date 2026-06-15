package com.smartlogix.envios.controller;

import com.smartlogix.envios.model.Envio;
import com.smartlogix.envios.model.EstadoEnvio;
import com.smartlogix.envios.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping("")
    public ResponseEntity<Envio> registrarEnvio(@Valid @RequestBody Envio envio) {
        try {
            Envio creado = envioService.registrarEnvio(envio);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Envio>> listarEnvios() {
        return new ResponseEntity<>(envioService.listarTodos(), HttpStatus.OK);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Envio>> listarEnviosDisponibles() {
        return new ResponseEntity<>(envioService.listarDisponibles(), HttpStatus.OK);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Envio> cambiarEstado(
            @NonNull @PathVariable String id,
            @NonNull @RequestParam EstadoEnvio estado) {
        try {
            return new ResponseEntity<>(envioService.cambiarEstado(id, estado), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}

