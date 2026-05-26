package ms_logistics_base.service;

import ms_logistics_base.model.Pedido;
import ms_logistics_base.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PedidoServiceImpl implements PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${ms.inventario.base-url}")
	private String inventarioBaseUrl;

	@Override
	public Pedido registrarPedido(Pedido pedido) {
		if (pedido.getCantidadSolicitada() == null) {
			throw new IllegalArgumentException("La cantidad solicitada no puede ser nula");
		}

		if (pedido.getCantidadSolicitada() <= 0) {
			throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero");
		}

		if (pedido.getProductoId() == null || pedido.getProductoId().trim().isEmpty()) {
			throw new IllegalArgumentException("productoId es requerido");
		}

		String skuProducto = obtenerSkuDesdeProductoId(pedido.getProductoId());
		pedido.setSkuProducto(skuProducto);

		validarStockDisponible(pedido.getProductoId(), pedido.getCantidadSolicitada());

		Pedido guardado = pedidoRepository.save(pedido);
		try {
			descontarStockPorId(pedido.getProductoId(), pedido.getCantidadSolicitada());
		} catch (IllegalArgumentException ex) {
			pedidoRepository.delete(guardado);
			throw ex;
		}

		return guardado;
	}

	private String obtenerSkuDesdeProductoId(String productoId) {
		try {
			ResponseEntity<Map> respuesta = restTemplate.getForEntity(
				inventarioBaseUrl + "/api/productos/buscar-por-id?id={id}",
				Map.class,
				productoId
			);
			if (!respuesta.getStatusCode().is2xxSuccessful() || respuesta.getBody() == null) {
				throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
			}
			Object sku = respuesta.getBody().get("sku");
			if (sku == null || String.valueOf(sku).trim().isEmpty()) {
				throw new IllegalArgumentException("Producto inválido: no contiene SKU (ID: " + productoId + ")");
			}
			return String.valueOf(sku);
		} catch (HttpStatusCodeException ex) {
			throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
		} catch (ResourceAccessException ex) {
			throw new IllegalArgumentException("No se pudo contactar ms-logistics-base para obtener el producto");
		}
	}

	private void validarStockDisponible(String productoId, int cantidad) {
		try {
			ResponseEntity<Map> respuesta = restTemplate.getForEntity(
				inventarioBaseUrl + "/api/productos/buscar-por-id?id={id}",
				Map.class,
				productoId
			);
			if (!respuesta.getStatusCode().is2xxSuccessful() || respuesta.getBody() == null) {
				throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
			}
			Object stock = respuesta.getBody().get("cantidadStock");
			if (stock == null) {
				stock = respuesta.getBody().get("cantidad_stock");
			}
			int stockActual = Integer.parseInt(String.valueOf(stock));
			if (stockActual < cantidad) {
				throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + stockActual);
			}
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("No se pudo validar el stock del producto");
		} catch (HttpStatusCodeException ex) {
			throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
		} catch (ResourceAccessException ex) {
			throw new IllegalArgumentException("No se pudo contactar ms-logistics-base para validar stock");
		}
	}

	private void descontarStockPorId(String productoId, int cantidad) {
		try {
			restTemplate.exchange(
				inventarioBaseUrl + "/api/productos/{id}/descontar-stock?cantidad={cantidad}",
				HttpMethod.PUT,
				HttpEntity.EMPTY,
				Object.class,
				productoId,
				cantidad
			);
		} catch (HttpStatusCodeException ex) {
			throw new IllegalArgumentException("No se pudo descontar stock para productoId: " + productoId);
		} catch (ResourceAccessException ex) {
			throw new IllegalArgumentException("No se pudo contactar ms-logistics-base para actualizar el stock");
		}
	}

	@Override
	public List<Pedido> listarTodos() {
		return pedidoRepository.findAll();
	}

	@Override
	public Pedido actualizarEstado(String numeroPedido, String nuevoEstado) {
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);

		if (pedido == null) {
			throw new IllegalArgumentException("Pedido no encontrado con número: " + numeroPedido);
		}

		if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
			throw new IllegalArgumentException("El estado del pedido no puede estar vacío");
		}

		pedido.setEstado(nuevoEstado);
		return pedidoRepository.save(pedido);
	}

	@Override
	public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El ID del pedido no puede estar vacío o ser inválido");
		}

		Pedido pedido = pedidoRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

		if (pedidoActualizado.getNumeroPedido() != null && !pedidoActualizado.getNumeroPedido().isEmpty()) {
			pedido.setNumeroPedido(pedidoActualizado.getNumeroPedido());
		}
		if (pedidoActualizado.getCliente() != null && !pedidoActualizado.getCliente().isEmpty()) {
			pedido.setCliente(pedidoActualizado.getCliente());
		}
		if (pedidoActualizado.getDescripcion() != null && !pedidoActualizado.getDescripcion().isEmpty()) {
			pedido.setDescripcion(pedidoActualizado.getDescripcion());
		}
		if (pedidoActualizado.getSkuProducto() != null && !pedidoActualizado.getSkuProducto().isEmpty()) {
			pedido.setSkuProducto(pedidoActualizado.getSkuProducto());
		}
		if (pedidoActualizado.getCantidadSolicitada() != null) {
			if (pedidoActualizado.getCantidadSolicitada() <= 0) {
				throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero");
			}
			pedido.setCantidadSolicitada(pedidoActualizado.getCantidadSolicitada());
		}
		if (pedidoActualizado.getEstado() != null && !pedidoActualizado.getEstado().isEmpty()) {
			pedido.setEstado(pedidoActualizado.getEstado());
		}

		return pedidoRepository.save(pedido);
	}

	@Override
	public void eliminarPedido(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El ID del pedido no puede estar vacío o ser inválido");
		}

		Pedido pedido = pedidoRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

		pedidoRepository.delete(pedido);
	}
}
