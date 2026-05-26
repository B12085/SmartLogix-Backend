# SmartLogix - Microservicio de Pedidos (Puerto 8082)

Módulo especializado en recepción, registro y ciclo de vida de órdenes de compra solicitadas por clientes. Funciona de forma aislada del módulo de inventario pero se integra con él mediante REST calls para validar disponibilidad y descontar stock.

## Stack y Arquitectura

Implementado con Java 17 y Spring Boot 4 manteniendo la misma filosofía de código explícito: POJOs puros sin Lombok, validaciones de negocio mediante condicionales tradicionales. La cantidad solicitada debe ser siempre mayor a cero (validación ética), y la base de datos (`db_smartlogix_pedidos`) corre sobre MySQL de forma independiente.

## Endpoints REST

### Registrar Nueva Orden
```
POST /api/pedidos/registrar
Content-Type: application/json
```

Solicitud:
```json
{
  "productoId": "prod-123",
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Laptop Dell XPS 13",
  "cantidadSolicitada": 2,
  "estado": "PENDIENTE"
}
```

Respuesta (201 Created):
```json
{
  "id": 1,
  "productoId": "prod-123",
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Laptop Dell XPS 13",
  "skuProducto": "SKU-DELL-XPS13",
  "cantidadSolicitada": 2,
  "estado": "PENDIENTE"
}
```

Validaciones aplicadas:
- Cantidad solicitada > 0
- Número de pedido único
- `productoId` requerido y debe existir en ms-logistics-base
- Stock disponible suficiente
- Ningún campo requerido puede ser nulo

### Listar Órdenes
```
GET /api/pedidos/listar
```

Respuesta (200 OK):
```json
[
  {
    "id": 1,
    "productoId": "prod-001",
    "numeroPedido": "PED-001",
    "cliente": "Juan Pérez",
    "descripcion": "Laptop Dell XPS 13",
    "skuProducto": "SKU-DELL-XPS13",
    "cantidadSolicitada": 2,
    "estado": "PENDIENTE"
  }
]
```

### Actualizar Estado
```
PUT /api/pedidos/actualizar-estado?numeroPedido=PED-001&nuevoEstado=PROCESADO
```

Estados válidos:
- `PENDIENTE` — Orden recibida, en espera
- `PROCESADO` — En fase de preparación y empaque
- `ENVIADO` — Entregada al transportista
- `ENTREGADO` — Recibida por el cliente

Respuesta (200 OK):
```json
{
  "id": 1,
  "productoId": "prod-001",
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Laptop Dell XPS 13",
  "skuProducto": "SKU-DELL-XPS13",
  "cantidadSolicitada": 2,
  "estado": "PROCESADO"
}
```

## Configuración de Base de Datos

### Script SQL
```sql
CREATE DATABASE IF NOT EXISTS db_smartlogix_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_smartlogix_pedidos;

CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    cliente VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    cantidad_solicitada INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    CONSTRAINT chk_cantidad CHECK (cantidad_solicitada > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_numero_pedido ON pedidos(numero_pedido);
CREATE INDEX idx_estado ON pedidos(estado);
```

### application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_smartlogix_pedidos
spring.datasource.username=root
spring.datasource.password=tu_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

server.port=8082
```

## Estructura del Proyecto

```
src/main/java/ms_logistics_base/
├── controller/
│   └── PedidoController.java
├── service/
│   ├── PedidoService.java
│   └── PedidoServiceImpl.java
├── model/
│   └── Pedido.java
├── repository/
│   └── PedidoRepository.java
├── config/
│   └── SecurityConfig.java
└── MsLogisticsBaseApplication.java
```

## Ejemplos cURL

Crear orden válida:
```bash
curl -X POST http://localhost:8082/api/pedidos/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": "prod-001",
    "numeroPedido": "PED-001",
    "cliente": "Carlos López",
    "descripcion": "Mouse inalámbrico Logitech",
    "cantidadSolicitada": 5,
    "estado": "PENDIENTE"
  }'
```

Rechaza cantidad inválida:
```bash
curl -X POST http://localhost:8082/api/pedidos/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": "prod-002",
    "numeroPedido": "PED-002",
    "cliente": "Ana Martínez",
    "descripcion": "Teclado mecánico",
    "cantidadSolicitada": 0,
    "estado": "PENDIENTE"
  }'
```

Listar todas las órdenes:
```bash
curl http://localhost:8082/api/pedidos/listar
```

Actualizar estado:
```bash
curl -X PUT "http://localhost:8082/api/pedidos/actualizar-estado?numeroPedido=PED-001&nuevoEstado=ENVIADO"
```

## Notas

- Código limpio y explícito sin anotaciones complejas como Lombok.
- Validación ética mediante condicionales tradicionales.
- Patrón MVC claro: Controlador → Servicio → Repositorio.
- Base de datos dedicada separada del módulo de inventario.
- Integración REST con ms-logistics-base para validar stock y descontar existencias.

---

**Estado:** Microservicio Listo para Integración  
**Versión:** 0.0.1-SNAPSHOT  
**Puerto:** 8082  
**Tecnología:** Spring Boot 4 + Java 17 + MySQL
