package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.model.Pedido;
import com.smartlogix.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;

class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
    }


    @Test
    void registrarPedido_conSnakeCase_retorna201() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setNumeroPedido("PED-001");
        pedido.setCliente("Cliente");
        pedido.setDescripcion("Desc");
        pedido.setProductoId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        pedido.setSkuProducto("SKU001");
        pedido.setCantidadSolicitada(2);

        when(pedidoService.registrarPedido(ArgumentMatchers.<Pedido>any())).thenReturn(pedido);

        mockMvc.perform(
                        post("/api/pedidos/registrar")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("""
                                        {
                                          "numero_pedido": "PED-001",
                                          "cliente": "Cliente",
                                          "descripcion": "Desc",
                                          "producto_id": "c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a",
                                          "sku_producto": "SKU001",
                                          "cantidad_solicitada": 2
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero_pedido").value("PED-001"))
                .andExpect(jsonPath("$.producto_id").value("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a"))
                .andExpect(jsonPath("$.sku_producto").value("SKU001"))
                .andExpect(jsonPath("$.cantidad_solicitada").value(2));
    }

    @Test
    void listarPedidos_retorna200() throws Exception {
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setNumeroPedido("PED-001");
        pedido1.setCliente("Cliente 1");
        pedido1.setDescripcion("Descripción 1");
        pedido1.setProductoId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        pedido1.setSkuProducto("SKU001");
        pedido1.setCantidadSolicitada(2);

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setNumeroPedido("PED-002");
        pedido2.setCliente("Cliente 2");
        pedido2.setDescripcion("Descripción 2");
        pedido2.setProductoId("d4c8f72b-4dac-5b6g-0c0d-2d1e9b2e1d3b");
        pedido2.setSkuProducto("SKU002");
        pedido2.setCantidadSolicitada(5);

        when(pedidoService.listarTodos()).thenReturn(Arrays.asList(pedido1, pedido2));

        mockMvc.perform(get("/api/pedidos/listar")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero_pedido").value("PED-001"))
                .andExpect(jsonPath("$[0].cliente").value("Cliente 1"))
                .andExpect(jsonPath("$[0].cantidad_solicitada").value(2))
                .andExpect(jsonPath("$[1].numero_pedido").value("PED-002"))
                .andExpect(jsonPath("$[1].cliente").value("Cliente 2"))
                .andExpect(jsonPath("$[1].cantidad_solicitada").value(5));
    }

    @Test
    void actualizarEstadoPedido_retorna200() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setNumeroPedido("PED-001");
        pedido.setCliente("Cliente");
        pedido.setDescripcion("Desc");
        pedido.setProductoId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        pedido.setSkuProducto("SKU001");
        pedido.setCantidadSolicitada(2);

        when(pedidoService.actualizarEstado("PED-001", "ENTREGADO")).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/actualizar-estado")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("numeroPedido", "PED-001")
                        .param("nuevoEstado", "ENTREGADO")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero_pedido").value("PED-001"))
                .andExpect(jsonPath("$.cliente").value("Cliente"))
                .andExpect(jsonPath("$.cantidad_solicitada").value(2));
    }
}

