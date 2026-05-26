package ms_logistics_base.service;

import ms_logistics_base.model.Producto;
import org.springframework.lang.NonNull;
import java.util.List;

public interface ProductoService {

    Producto registrarProducto(@NonNull Producto producto);

    List<Producto> listarTodos();

    Producto buscarPorSku(@NonNull String codigoSku);

    Producto buscarPorId(@NonNull String id);

    Producto reducirStockPorId(@NonNull String id, @NonNull Integer cantidad);

    Producto actualizarStock(@NonNull String codigoSku, @NonNull Integer cantidadARestar);

    Producto actualizarProducto(@NonNull String codigoSku, @NonNull Producto producto);

    void eliminarProducto(@NonNull String codigoSku);
}
