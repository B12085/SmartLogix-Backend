package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.model.Pedido;
import java.util.List;

public interface PedidoService {

	Pedido registrarPedido(Pedido pedido);

	List<Pedido> listarTodos();

	Pedido actualizarEstado(String numeroPedido, String nuevoEstado);

	Pedido actualizarPedido(Long id, Pedido pedido);

	void eliminarPedido(Long id);
}

