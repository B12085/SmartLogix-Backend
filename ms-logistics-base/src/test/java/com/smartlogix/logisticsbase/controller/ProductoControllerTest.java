package com.smartlogix.logisticsbase.controller;

import com.smartlogix.logisticsbase.model.Producto;
import com.smartlogix.logisticsbase.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.smartlogix.logisticsbase.config.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;

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

        when(productoService.registrarProducto(ArgumentMatchers.<Producto>any())).thenReturn(producto);

        mockMvc.perform(
                        post("/api/productos/registrar")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("cantidad", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadStock").value(7))
                .andExpect(jsonPath("$.id").value(producto.getId()));
    }

    @Test
    void listarProductos_retorna200() throws Exception {
        Producto producto1 = new Producto("SKU001", "Producto 1", "Descripción 1", 10, 100.0);
        producto1.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");
        Producto producto2 = new Producto("SKU002", "Producto 2", "Descripción 2", 20, 200.0);
        producto2.setId("d4c8f72b-4dac-5b6g-0c0d-2d1e9b2e1d3b");

        when(productoService.listarTodos()).thenReturn(Arrays.asList(producto1, producto2));

        mockMvc.perform(get("/api/productos/listar")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("SKU001"))
                .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
                .andExpect(jsonPath("$[0].cantidadStock").value(10))
                .andExpect(jsonPath("$[1].sku").value("SKU002"))
                .andExpect(jsonPath("$[1].nombre").value("Producto 2"))
                .andExpect(jsonPath("$[1].cantidadStock").value(20));
    }

    @Test
    void buscarProductoPorSku_retorna200() throws Exception {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 10, 100.0);
        producto.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");

        when(productoService.buscarPorSku("SKU001")).thenReturn(producto);

        mockMvc.perform(get("/api/productos/buscar")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("sku", "SKU001")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU001"))
                .andExpect(jsonPath("$.nombre").value("Producto"))
                .andExpect(jsonPath("$.precio").value(100.0));
    }
}

