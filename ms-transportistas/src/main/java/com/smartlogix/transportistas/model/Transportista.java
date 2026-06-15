package com.smartlogix.transportistas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

// Entidad que representa un transportista en el sistema
@Entity
@Table(name = "transportista")
public class Transportista {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "El nombre no puede ser nulo")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre")
    private String nombre;

    @NotNull(message = "La patente no puede ser nula")
    @NotBlank(message = "La patente no puede estar vacía")
    @Column(name = "patente", unique = true)
    @Pattern(regexp = "^[A-Z]{2,3}-\\d{3,4}$", message = "La patente debe tener formato válido (ej: ABC-1234)")
    private String patente;

    @NotNull(message = "El teléfono no puede ser nulo")
    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(name = "telefono")
    @Pattern(regexp = "^\\d{7,}$", message = "El teléfono debe contener al menos 7 dígitos")
    private String telefono;

    @NotNull(message = "El estado de disponibilidad no puede ser nulo")
    @Column(name = "disponible")
    private Boolean disponible;

    public Transportista() {
    }

    public Transportista(String nombre, String patente, String telefono, Boolean disponible) {
        this.nombre = nombre;
        this.patente = patente;
        this.telefono = telefono;
        this.disponible = disponible;
    }

    public Transportista(String id, String nombre, String patente, String telefono, Boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.patente = patente;
        this.telefono = telefono;
        this.disponible = disponible;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.disponible == null) {
            this.disponible = true;
        }
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Transportista{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", patente='" + patente + '\'' +
                ", telefono='" + telefono + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}

