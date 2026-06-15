package com.smartlogix.pedidos.repository;

import com.smartlogix.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio de acceso a datos para las órdenes de compra
// Proporciona operaciones CRUD automáticas y métodos de consulta personalizados
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	// Busca una orden de compra por su número único
	Pedido findByNumeroPedido(String numeroPedido);
}

