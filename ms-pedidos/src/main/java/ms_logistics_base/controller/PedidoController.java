package ms_logistics_base.controller;

import ms_logistics_base.model.Pedido;
import ms_logistics_base.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	@Autowired
	private PedidoService pedidoService;

	@PostMapping("/registrar")
	public ResponseEntity<Pedido> registrar(@Valid @RequestBody Pedido pedido) {
		try {
			Pedido nuevoPedido = pedidoService.registrarPedido(pedido);
			return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
		} catch (IllegalArgumentException excepcion) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Pedido>> listar() {
		List<Pedido> listaPedidos = pedidoService.listarTodos();
		return new ResponseEntity<>(listaPedidos, HttpStatus.OK);
	}

	@PutMapping("/actualizar-estado")
	public ResponseEntity<Pedido> actualizarEstado(
			@RequestParam String numeroPedido,
			@RequestParam String nuevoEstado) {
		try {
			Pedido pedidoActualizado = pedidoService.actualizarEstado(numeroPedido, nuevoEstado);
			return new ResponseEntity<>(pedidoActualizado, HttpStatus.OK);
		} catch (IllegalArgumentException excepcion) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/actualizar/{id}")
	public ResponseEntity<Pedido> actualizarPedido(
			@PathVariable Long id,
			@Valid @RequestBody Pedido pedido) {
		try {
			Pedido pedidoActualizado = pedidoService.actualizarPedido(id, pedido);
			return new ResponseEntity<>(pedidoActualizado, HttpStatus.OK);
		} catch (IllegalArgumentException excepcion) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
		try {
			pedidoService.eliminarPedido(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException excepcion) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
