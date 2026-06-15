package com.smartlogix.logisticsbase.service;

import com.smartlogix.logisticsbase.model.Producto;
import com.smartlogix.logisticsbase.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void reducirStockPorId_descuentaYGuarda() {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 10, 100.0);
        producto.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");

        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));
        when(productoRepository.save(ArgumentMatchers.<Producto>any())).thenAnswer(invocation -> invocation.getArgument(0));

        Producto actualizado = productoService.reducirStockPorId(producto.getId(), 3);

        assertEquals(7, actualizado.getCantidadStock());
    }

    @Test
    void reducirStockPorId_stockInsuficiente_lanzaExcepcion() {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 2, 100.0);
        producto.setId("c3b7f61a-3c9b-4a5f-9b9c-1c0d8a1d0c2a");

        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> productoService.reducirStockPorId(producto.getId(), 3));
    }

    @Test
    void registrarProducto_precioNegativo_lanzaExcepcion() {
        Producto producto = new Producto("SKU001", "Producto", "Desc", 10, -1.0);
        assertThrows(IllegalArgumentException.class, () -> productoService.registrarProducto(producto));
    }
}

