# ms-pedidos - Microservicio de Órdenes de Compra

Módulo especializado en recepción, registro y ciclo de vida de órdenes de compra. Integrado con ms-logistics-base mediante REST calls para validar disponibilidad y descontar stock.

## 📋 Especificaciones Técnicas

| Propiedad | Valor |
|-----------|-------|
| **Puerto** | 8082 |
| **Spring Boot** | 3.3.0 |
| **Java** | 21 LTS |
| **Base de Datos** | MySQL (smartlogix) |
| **Tabla Principal** | pedido |
| **ORM** | Spring Data JPA + Hibernate |
| **OpenAPI** | SpringDoc 2.3.0 |

## 🛠️ Stack y Arquitectura

**Tecnologías**:
- Java 21 LTS
- Spring Boot 3.3.0 con Spring Data JPA
- RestTemplate para integración con ms-logistics-base
- MySQL para persistencia
- JaCoCo v0.8.11 para cobertura de código

**Características**:
- Código POJO explícito (sin Lombok)
- Validaciones de negocio: cantidad > 0, stock disponible
- Integración REST con ms-logistics-base
- Estados de pedido: PENDIENTE, PROCESANDO, COMPLETADO
- Migrations automáticas de esquema

## 🔌 Endpoints REST

### Registrar Nueva Orden
```http
POST /api/pedidos/registrar
Content-Type: application/json
```

**Solicitud**:
```json
{
  "cliente": "Juan Pérez",
  "descripcion": "Compra de equipos",
  "cantidadSolicitada": 5,
  "estado": "PENDIENTE"
}
```

**Validaciones**:
- cantidadSolicitada > 0 (siempre positivo)
- cliente no vacío
- Se valida stock en ms-logistics-base
- Número de pedido generado automáticamente

**Respuesta (201 Created)**:
```json
{
  "id": 1,
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Compra de equipos",
  "cantidadSolicitada": 5,
  "estado": "PENDIENTE",
  "fecha": "2025-06-15T10:30:00"
}
```

### Listar Órdenes
```http
GET /api/pedidos/listar
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": 1,
    "numeroPedido": "PED-001",
    "cliente": "Juan Pérez",
    "descripcion": "Compra de equipos",
    "cantidadSolicitada": 5,
    "estado": "PENDIENTE",
    "fecha": "2025-06-15T10:30:00"
  },
  {
    "id": 2,
    "numeroPedido": "PED-002",
    "cliente": "María García",
    "descripcion": "Reabastecimiento",
    "cantidadSolicitada": 10,
    "estado": "PROCESANDO",
    "fecha": "2025-06-15T11:00:00"
  }
]
```

### Buscar Pedido por ID
```http
GET /api/pedidos/{id}
```

**Parámetros de ruta**:
- `id` (requerido): ID del pedido

**Respuesta (200 OK)**:
```json
{
  "id": 1,
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Compra de equipos",
  "cantidadSolicitada": 5,
  "estado": "PENDIENTE",
  "fecha": "2025-06-15T10:30:00"
}
```

### Actualizar Estado
```http
PUT /api/pedidos/{id}/estado
Content-Type: application/json

{
  "estado": "PROCESANDO"
}
```

**Estados válidos**: PENDIENTE | PROCESANDO | COMPLETADO

**Respuesta (200 OK)**:
```json
{
  "id": 1,
  "numeroPedido": "PED-001",
  "cliente": "Juan Pérez",
  "descripcion": "Compra de equipos",
  "cantidadSolicitada": 5,
  "estado": "PROCESANDO",
  "fecha": "2025-06-15T10:30:00"
}
```

## 🧪 Pruebas

El servicio incluye **4 pruebas unitarias** con cobertura JaCoCo:

```bash
# Ejecutar pruebas
mvn test

# Ver cobertura
mvn test jacoco:report
# Acceder a: target/site/jacoco/index.html
```

**Tests incluidos**:
1. Registrar pedido válido
2. Listar pedidos
3. Buscar pedido por ID
4. Cambiar estado del pedido

## 🚀 Ejecutar Localmente

```bash
# Instalar dependencias
mvn clean install

# Ejecutar servicio (requiere ms-logistics-base)
mvn spring-boot:run
```

El servicio estará disponible en:
- **API**: http://localhost:8082/api/pedidos
- **Swagger**: http://localhost:8082/swagger-ui.html
- **Health**: http://localhost:8082/actuator/health

## 🔗 Dependencias

**Requiere ms-logistics-base activo en:**
- http://localhost:8081

Esta integración se usa para:
- Validar disponibilidad de stock
- Consultar detalles de productos
- Descontar automáticamente del inventario

## 📊 Estructura de Base de Datos

**Tabla: pedido**
```sql
CREATE TABLE pedido (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  numero_pedido VARCHAR(50) UNIQUE NOT NULL,
  cliente VARCHAR(255) NOT NULL,
  descripcion TEXT,
  cantidad_solicitada INT NOT NULL,
  estado VARCHAR(20) NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT check_cantidad CHECK (cantidad_solicitada > 0)
);
```

## 📝 Flujo de Integración

1. **Frontend** crea un pedido → POST /api/pedidos/registrar
2. **ms-pedidos** valida cantidad > 0
3. **ms-pedidos** consulta **ms-logistics-base** para verificar stock
4. Si hay stock disponible:
   - Registra el pedido como PENDIENTE
   - Descuenta automáticamente del inventario
5. Si NO hay stock:
   - Rechaza la solicitud con error 400

## 📝 Notas

- Integración automática con ms-logistics-base
- Validaciones a nivel de servicio
- CORS configurado para localhost:5173
- Swagger disponible automáticamente
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
