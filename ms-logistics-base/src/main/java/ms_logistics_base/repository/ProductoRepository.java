package ms_logistics_base.repository;

import ms_logistics_base.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Interfaz del repositorio para operaciones de base de datos con la entidad Producto
@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    // Método personalizado para buscar un producto por su SKU
    // Retorna Optional para manejar casos donde el producto no existe
    Optional<Producto> findBySku(String sku);

    Optional<Producto> findByCodigoSku(String codigoSku);
}
