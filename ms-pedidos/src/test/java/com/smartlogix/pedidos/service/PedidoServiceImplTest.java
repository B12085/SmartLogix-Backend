package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private @NonNull PedidoServiceImpl pedidoService;

    @SuppressWarnings("unchecked")
    private static <T> Class<T> clazz(Class<?> type) {
        return (Class<T>) type;
    }

    @Test
    void registrarPedido_guardaYDescuentaStock() {
        ReflectionTestUtils.setField(pedidoService, "inventarioBaseUrl", "http://localhost:8081");

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED-001");
        pedido.setCliente("Cliente");
        pedido.setDescripcion("Desc");
        pedido.setProductoId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        pedido.setCantidadSolicitada(2);

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/productos/buscar-por-id?id={id}"),
                ArgumentMatchers.<HttpMethod>eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any(),
                eq(pedido.getProductoId())
        )).thenReturn(ResponseEntity.ok(Map.of("sku", "SKU001", "cantidadStock", 10)));

        Pedido guardado = new Pedido();
        guardado.setId(1L);
        when(pedidoRepository.save(ArgumentMatchers.<Pedido>any())).thenReturn(guardado);

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/productos/{id}/descontar-stock?cantidad={cantidad}"),
                ArgumentMatchers.<HttpMethod>eq(HttpMethod.PUT),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>eq(clazz(Object.class)),
                eq(pedido.getProductoId()),
                eq(pedido.getCantidadSolicitada())
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        pedidoService.registrarPedido(pedido);

        verify(pedidoRepository, times(1)).save(ArgumentMatchers.<Pedido>any());
    }

    @Test
    void registrarPedido_siFallaDescuento_reviertePedido() {
        ReflectionTestUtils.setField(pedidoService, "inventarioBaseUrl", "http://localhost:8081");

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED-001");
        pedido.setCliente("Cliente");
        pedido.setProductoId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        pedido.setCantidadSolicitada(2);

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/productos/buscar-por-id?id={id}"),
                ArgumentMatchers.<HttpMethod>eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any(),
                eq(pedido.getProductoId())
        )).thenReturn(ResponseEntity.ok(Map.of("sku", "SKU001", "cantidadStock", 10)));

        Pedido guardado = new Pedido();
        guardado.setId(1L);
        when(pedidoRepository.save(ArgumentMatchers.<Pedido>any())).thenReturn(guardado);

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/productos/{id}/descontar-stock?cantidad={cantidad}"),
                ArgumentMatchers.<HttpMethod>eq(HttpMethod.PUT),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>eq(clazz(Object.class)),
                eq(pedido.getProductoId()),
                eq(pedido.getCantidadSolicitada())
        )).thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, null, null));

        assertThrows(IllegalArgumentException.class, () -> pedidoService.registrarPedido(pedido));
        verify(pedidoRepository, times(1)).delete(guardado);
    }
}


