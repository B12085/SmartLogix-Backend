package com.smartlogix.logisticsbase.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

// Entidad que representa un producto en el inventario
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @NotNull(message = "El SKU no puede ser nulo")
    @NotBlank(message = "El SKU no puede estar vacío")
    @Column(unique = true)
    private String sku;

    @Column(name = "codigo_sku", unique = true)
    private String codigoSku;

    @NotNull(message = "El nombre no puede ser nulo")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La cantidad de stock no puede ser nula")
    @Positive(message = "La cantidad de stock debe ser mayor a 0")
    @Column(name = "cantidad_stock")
    private Integer cantidadStock;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    public Producto() {
    }

    public Producto(String sku, String nombre, String descripcion,
                   Integer cantidadStock, Double precio) {
        setSku(sku);
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidadStock = cantidadStock;
        this.precio = precio;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if ((this.sku == null || this.sku.trim().isEmpty()) && this.codigoSku != null && !this.codigoSku.trim().isEmpty()) {
            this.sku = this.codigoSku;
        }
        if ((this.codigoSku == null || this.codigoSku.trim().isEmpty()) && this.sku != null && !this.sku.trim().isEmpty()) {
            this.codigoSku = this.sku;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("sku")
    public String getSku() {
        if (sku != null && !sku.trim().isEmpty() && !"NULL".equalsIgnoreCase(sku.trim())) {
            return sku;
        }
        if (codigoSku != null && !codigoSku.trim().isEmpty() && !"NULL".equalsIgnoreCase(codigoSku.trim())) {
            return codigoSku;
        }
        return sku;
    }

    @JsonProperty("sku")
    @JsonAlias({ "codigo_sku", "codigoSku" })
    public void setSku(String sku) {
        this.sku = sku;
        this.codigoSku = sku;
    }

    @JsonIgnore
    public String getCodigoSku() {
        return codigoSku;
    }

    @JsonIgnore
    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
        this.sku = codigoSku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(Integer cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}

