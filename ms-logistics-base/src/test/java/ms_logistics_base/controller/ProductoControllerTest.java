package ms_logistics_base.controller;

import ms_logistics_base.model.Producto;
import ms_logistics_base.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import ms_logistics_base.config.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductoController.class,
  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Test
    void registrarProducto_retorna201() throws Exception {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 10, 100.0);
        producto.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");

        when(productoService.registrarProducto(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(
                        post("/api/productos/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "sku": "SKU001",
                                          "nombre": "Producto",
                                          "descripcion": "Desc",
                                          "cantidadStock": 10,
                                          "precio": 100.0
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU001"))
                .andExpect(jsonPath("$.id").value(producto.getId()));
    }

    @Test
    void descontarStockPorId_retorna200() throws Exception {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 7, 100.0);
        producto.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");

        when(productoService.reducirStockPorId(eq(producto.getId()), eq(3))).thenReturn(producto);

        mockMvc.perform(
                        put("/api/productos/{id}/descontar-stock", producto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("cantidad", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadStock").value(7))
                .andExpect(jsonPath("$.id").value(producto.getId()));
    }
}
