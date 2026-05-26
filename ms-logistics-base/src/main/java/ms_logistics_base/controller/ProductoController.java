package ms_logistics_base.controller;

import ms_logistics_base.model.Producto;
import ms_logistics_base.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/registrar")
    public ResponseEntity<Producto> registrarProducto(@Valid @NonNull @RequestBody Producto producto) {
        try {
            Producto productoRegistrado = productoService.registrarProducto(producto);
            return new ResponseEntity<>(productoRegistrado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoService.listarTodos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Producto> buscarPorSku(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String codigoSku) {
        try {
            String valorSku = (sku != null && !sku.isBlank()) ? sku : codigoSku;
            if (valorSku == null || valorSku.isBlank()) {
                throw new IllegalArgumentException("SKU requerido");
            }
            Producto producto = productoService.buscarPorSku(valorSku);
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/buscar-por-id")
    public ResponseEntity<Producto> buscarPorId(@NonNull @RequestParam String id) {
        try {
            Producto producto = productoService.buscarPorId(id);
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<Producto> reducirStockPorId(
            @NonNull @PathVariable String id,
            @NonNull @RequestParam Integer cantidad) {
        try {
            Producto productoActualizado = productoService.reducirStockPorId(id, cantidad);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStockPorId(
            @NonNull @PathVariable String id,
            @NonNull @RequestParam Integer cantidad) {
        try {
            Producto productoActualizado = productoService.reducirStockPorId(id, cantidad);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/actualizar-stock")
    public ResponseEntity<Producto> actualizarStock(
            @NonNull @RequestParam String sku,
            @NonNull @RequestParam Integer cantidad) {
        try {
            Producto productoActualizado = productoService.actualizarStock(sku, cantidad);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/actualizar/{sku}")
    public ResponseEntity<Producto> actualizarProducto(
            @NonNull @PathVariable String sku,
            @Valid @NonNull @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(sku, producto);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/eliminar/{sku}")
    public ResponseEntity<Void> eliminarProducto(@NonNull @PathVariable String sku) {
        try {
            productoService.eliminarProducto(sku);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
