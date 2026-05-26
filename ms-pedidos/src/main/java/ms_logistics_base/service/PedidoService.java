package ms_logistics_base.service;

import ms_logistics_base.model.Pedido;
import java.util.List;

public interface PedidoService {

	Pedido registrarPedido(Pedido pedido);

	List<Pedido> listarTodos();

	Pedido actualizarEstado(String numeroPedido, String nuevoEstado);

	Pedido actualizarPedido(Long id, Pedido pedido);

	void eliminarPedido(Long id);
}
