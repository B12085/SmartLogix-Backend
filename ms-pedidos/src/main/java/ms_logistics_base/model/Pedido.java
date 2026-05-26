package ms_logistics_base.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

// Entidad que representa una orden de compra en el microservicio de Pedidos de SmartLogix
@Entity
@Table(name = "pedidos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@Column(name = "producto_id", length = 36)
	@JsonProperty("producto_id")
	@JsonAlias({ "productoId" })
	private String productoId;

	@NotNull(message = "El número de pedido no puede ser nulo")
	@NotBlank(message = "El número de pedido no puede estar vacío")
	@Column(name = "numero_pedido", unique = true)
	@JsonProperty("numero_pedido")
	@JsonAlias({ "numeroPedido" })
	private String numeroPedido;

	@NotNull(message = "El cliente no puede ser nulo")
	@NotBlank(message = "El cliente no puede estar vacío")
	@JsonProperty("cliente")
	private String cliente;

	@JsonProperty("descripcion")
	private String descripcion;

	@Column(name = "sku_producto")
	@JsonProperty("sku_producto")
	@JsonAlias({ "skuProducto", "sku" })
	private String skuProducto;

	@NotNull(message = "La cantidad solicitada no puede ser nula")
	@Positive(message = "La cantidad solicitada debe ser mayor a 0")
	@Column(name = "cantidad_solicitada")
	@JsonProperty("cantidad_solicitada")
	@JsonAlias({ "cantidadSolicitada" })
	private Integer cantidadSolicitada;

	@Pattern(regexp = "PENDIENTE|PROCESADO|ENVIADO|ENTREGADO",
	         message = "El estado debe ser uno de: PENDIENTE, PROCESADO, ENVIADO, ENTREGADO")
	private String estado;

	@Column(name = "fecha_creacion")
	private LocalDateTime fecha;

	public Pedido() {
	}

	public Pedido(String numeroPedido, String cliente, String descripcion,
			String skuProducto, Integer cantidadSolicitada, String estado) {
		this.numeroPedido = numeroPedido;
		this.cliente = cliente;
		this.descripcion = descripcion;
		this.skuProducto = skuProducto;
		this.cantidadSolicitada = cantidadSolicitada;
		this.estado = estado;
	}

	@PrePersist
	protected void onCreate() {
		if (this.fecha == null) {
			this.fecha = LocalDateTime.now();
		}
		if (this.estado == null) {
			this.estado = "PENDIENTE";
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductoId() {
		return productoId;
	}

	public void setProductoId(String productoId) {
		this.productoId = productoId;
	}

	public String getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSkuProducto() {
		return skuProducto;
	}

	public void setSkuProducto(String skuProducto) {
		this.skuProducto = skuProducto;
	}

	public Integer getCantidadSolicitada() {
		return cantidadSolicitada;
	}

	public void setCantidadSolicitada(Integer cantidadSolicitada) {
		this.cantidadSolicitada = cantidadSolicitada;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
}
