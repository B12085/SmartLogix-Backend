package ms_logistics_base.service;

import ms_logistics_base.model.Producto;
import ms_logistics_base.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto registrarProducto(@NonNull Producto producto) {
        if (producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio del producto no puede ser negativo");
        }

        if (producto.getCantidadStock() < 0) {
            throw new IllegalArgumentException("La cantidad de stock no puede ser negativa");
        }

        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto buscarPorSku(@NonNull String sku) {
        Optional<Producto> productoOptional = productoRepository.findBySku(sku);
        if (productoOptional.isEmpty()) {
            productoOptional = productoRepository.findByCodigoSku(sku);
        }

        if (productoOptional.isPresent()) {
            return productoOptional.get();
        } else {
            throw new IllegalArgumentException("Producto no encontrado con SKU: " + sku);
        }
    }

    @Override
    public Producto buscarPorId(@NonNull String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de producto inválido");
        }

        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    @Override
    public Producto reducirStockPorId(@NonNull String id, @NonNull Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a 0");
        }

        Producto producto = buscarPorId(id);
        int nuevoStock = producto.getCantidadStock() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                    "No hay suficiente stock disponible. Stock actual: " + producto.getCantidadStock()
            );
        }
        producto.setCantidadStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarStock(@NonNull String sku, @NonNull Integer cantidadARestar) {
        Producto producto = buscarPorSku(sku);

        if (cantidadARestar < 0) {
            throw new IllegalArgumentException("La cantidad a restar no puede ser negativa");
        }

        int nuevoStock = producto.getCantidadStock() - cantidadARestar;

        if (nuevoStock < 0) {
            throw new IllegalArgumentException(
                "No hay suficiente stock disponible. Stock actual: " + producto.getCantidadStock()
            );
        }

        producto.setCantidadStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarProducto(@NonNull String sku, @NonNull Producto productoActualizado) {
        Producto producto = buscarPorSku(sku);

        if (productoActualizado.getNombre() != null && !productoActualizado.getNombre().isEmpty()) {
            producto.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getDescripcion() != null && !productoActualizado.getDescripcion().isEmpty()) {
            producto.setDescripcion(productoActualizado.getDescripcion());
        }
        if (productoActualizado.getPrecio() != null) {
            if (productoActualizado.getPrecio() < 0) {
                throw new IllegalArgumentException("El precio del producto no puede ser negativo");
            }
            producto.setPrecio(productoActualizado.getPrecio());
        }
        if (productoActualizado.getCantidadStock() != null) {
            if (productoActualizado.getCantidadStock() < 0) {
                throw new IllegalArgumentException("La cantidad de stock no puede ser negativa");
            }
            producto.setCantidadStock(productoActualizado.getCantidadStock());
        }

        return productoRepository.save(producto);
    }

    @Override
    public void eliminarProducto(@NonNull String sku) {
        Producto producto = buscarPorSku(sku);
        productoRepository.delete(producto);
    }
}
