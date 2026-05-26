package ms_logistics_base.controller;

import ms_logistics_base.model.Pedido;
import ms_logistics_base.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        when(pedidoService.registrarPedido(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(
                        post("/api/pedidos/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
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
}
