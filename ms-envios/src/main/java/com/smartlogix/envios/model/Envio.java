package com.smartlogix.envios.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "envios")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Envio {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "El pedidoId no puede ser nulo")
    @Column(name = "pedido_id")
    @JsonProperty("pedido_id")
    @JsonAlias({"pedidoId"})
    private Long pedidoId;

    @NotNull(message = "El transportistaId no puede ser nulo")
    @NotBlank(message = "El transportistaId no puede estar vacío")
    @Column(name = "transportista_id", length = 36)
    @JsonProperty("transportista_id")
    @JsonAlias({"transportistaId"})
    private String transportistaId;

    @NotNull(message = "La dirección de entrega no puede ser nula")
    @NotBlank(message = "La dirección de entrega no puede estar vacía")
    @Column(name = "direccion_entrega")
    @JsonProperty("direccion_entrega")
    @JsonAlias({"direccionEntrega"})
    private String direccionEntrega;

    @NotNull(message = "El estado no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEnvio estado;

    public Envio() {
    }

    public Envio(Long pedidoId, String transportistaId, String direccionEntrega, EstadoEnvio estado) {
        this.pedidoId = pedidoId;
        this.transportistaId = transportistaId;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
    }

    public Envio(String id, Long pedidoId, String transportistaId, String direccionEntrega, EstadoEnvio estado) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.transportistaId = transportistaId;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.estado == null) {
            this.estado = EstadoEnvio.PENDIENTE;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getTransportistaId() {
        return transportistaId;
    }

    public void setTransportistaId(String transportistaId) {
        this.transportistaId = transportistaId;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Envio{" +
                "id='" + id + '\'' +
                ", pedidoId=" + pedidoId +
                ", transportistaId='" + transportistaId + '\'' +
                ", direccionEntrega='" + direccionEntrega + '\'' +
                ", estado=" + estado +
                '}';
    }
}

