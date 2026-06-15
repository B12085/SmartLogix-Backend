# ms-envios - Microservicio de Envíos

Microservicio responsable del seguimiento y gestión de envíos. Permite registrar, listar y actualizar estados de envíos coordinados por transportistas en SmartLogix.

## 📋 Especificaciones Técnicas

| Propiedad | Valor |
|-----------|-------|
| **Puerto** | 8084 |
| **Spring Boot** | 3.3.0 |
| **Java** | 21 LTS |
| **Base de Datos** | MySQL (smartlogix) |
| **Tabla Principal** | envios |
| **ORM** | Spring Data JPA + Hibernate |
| **OpenAPI** | SpringDoc 2.3.0 |

## 🛠️ Stack y Arquitectura

**Tecnologías**:
- Java 21 LTS
- Spring Boot 3.3.0
- Spring Data JPA + Hibernate
- MySQL para persistencia
- JaCoCo v0.8.11 para cobertura de código

**Características**:
- Código POJO explícito
- Estados de envío: PENDIENTE → EN_RUTA → ENTREGADO
- Validaciones de datos de envío
- Tracking de envíos
- Migrations automáticas de esquema

## 📊 Modelo de Datos

### Entidad Envio

```java
@Entity
@Table(name = "envios")
public class Envio {
    @Id
    private String id;              // UUID auto-generado
    
    @Column(name = "pedido_id")
    private Long pedidoId;          // Referencia a pedido (ms-pedidos)
    
    @Column(name = "transportista_id")
    private String transportistaId;  // Transportista asignado (ms-transportistas)
    
    @Column(name = "direccion_entrega")
    private String direccionEntrega; // Destino del envío
    
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;      // PENDIENTE, EN_RUTA, ENTREGADO
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
```

### Estados de Envío

```java
public enum EstadoEnvio {
    PENDIENTE,    // Esperando asignación y preparación
    EN_RUTA,      // En camino al destino
    ENTREGADO     // Entregado al cliente
}
```

## 🔌 API REST

### Registrar Nuevo Envío
```http
POST /api/envios/registrar
Content-Type: application/json
```

**Solicitud**:
```json
{
  "pedido_id": 1,
  "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
  "direccion_entrega": "Calle Principal 123, Apartamento 4B",
  "estado": "PENDIENTE"
}
```

**Validaciones**:
- pedido_id requerido
- transportista_id requerido (UUID válido)
- direccion_entrega no vacía
- estado inicial debe ser PENDIENTE

**Respuesta (201 Created)**:
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "pedido_id": 1,
  "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
  "direccion_entrega": "Calle Principal 123, Apartamento 4B",
  "estado": "PENDIENTE",
  "fechaCreacion": "2025-06-15T10:30:00"
}
```

### Listar Todos los Envíos
```http
GET /api/envios/listar
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "pedido_id": 1,
    "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
    "direccion_entrega": "Calle Principal 123, Apartamento 4B",
    "estado": "PENDIENTE",
    "fechaCreacion": "2025-06-15T10:30:00"
  },
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "pedido_id": 2,
    "transportista_id": "550e8400-e29b-41d4-a716-446655440001",
    "direccion_entrega": "Calle Secundaria 456",
    "estado": "EN_RUTA",
    "fechaCreacion": "2025-06-15T11:00:00"
  },
  {
    "id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "pedido_id": 3,
    "transportista_id": "550e8400-e29b-41d4-a716-446655440002",
    "direccion_entrega": "Avenida Principal 789",
    "estado": "ENTREGADO",
    "fechaCreacion": "2025-06-14T09:00:00"
  }
]
```

### Listar Envíos Pendientes
```http
GET /api/envios/disponibles
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "pedido_id": 1,
    "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
    "direccion_entrega": "Calle Principal 123, Apartamento 4B",
    "estado": "PENDIENTE",
    "fechaCreacion": "2025-06-15T10:30:00"
  }
]
```

### Obtener Envío por ID
```http
GET /api/envios/{id}
```

**Parámetros de ruta**:
- `id` (requerido): UUID del envío

**Respuesta (200 OK)**:
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "pedido_id": 1,
  "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
  "direccion_entrega": "Calle Principal 123, Apartamento 4B",
  "estado": "PENDIENTE",
  "fechaCreacion": "2025-06-15T10:30:00"
}
```

### Actualizar Estado del Envío
```http
PUT /api/envios/{id}/estado
Content-Type: application/json

{
  "estado": "EN_RUTA"
}
```

**Parámetros de ruta**:
- `id` (requerido): UUID del envío

**Cambios de estado permitidos**:
- PENDIENTE → EN_RUTA
- EN_RUTA → ENTREGADO

**Respuesta (200 OK)**:
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "pedido_id": 1,
  "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
  "direccion_entrega": "Calle Principal 123, Apartamento 4B",
  "estado": "EN_RUTA",
  "fechaCreacion": "2025-06-15T10:30:00"
}
```

## 🧪 Pruebas

El servicio incluye **6 pruebas unitarias** con cobertura JaCoCo:

```bash
# Ejecutar pruebas
mvn test

# Ver cobertura
mvn test jacoco:report
# Acceder a: target/site/jacoco/index.html
```

**Tests incluidos**:
1. Registrar nuevo envío
2. Listar todos los envíos
3. Listar envíos disponibles (PENDIENTE)
4. Transición: PENDIENTE → EN_RUTA
5. Transición: EN_RUTA → ENTREGADO
6. Validaciones de datos obligatorios

## 🚀 Ejecutar Localmente

```bash
# Instalar dependencias
mvn clean install

# Ejecutar servicio
mvn spring-boot:run
```

El servicio estará disponible en:
- **API**: http://localhost:8084/api/envios
- **Swagger**: http://localhost:8084/swagger-ui.html
- **Health**: http://localhost:8084/actuator/health

## 📊 Estructura de Base de Datos

**Tabla: envios**
```sql
CREATE TABLE envios (
  id VARCHAR(36) PRIMARY KEY,
  pedido_id BIGINT NOT NULL,
  transportista_id VARCHAR(36) NOT NULL,
  direccion_entrega TEXT NOT NULL,
  estado VARCHAR(20) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (pedido_id) REFERENCES pedido(id),
  FOREIGN KEY (transportista_id) REFERENCES transportista(id)
);
```

## 🌐 Integración en SmartLogix

Este microservicio:
- Consume datos de **ms-pedidos** (IDs de pedidos)
- Consume datos de **ms-transportistas** (IDs y disponibilidad)
- Proporciona datos de seguimiento al **frontend-fullstack**
- Coordina la entrega de pedidos

## 📝 Flujo de Uso

1. **Frontend** registra un nuevo envío para un pedido
2. **ms-envios** valida que existan pedido y transportista
3. Estado inicial: PENDIENTE (esperando preparación)
4. Transportista marca como EN_RUTA (comenzó entrega)
5. Transportista marca como ENTREGADO (completado)
6. Frontend puede consultar estado en tiempo real

## 📝 Notas

- IDs generados automáticamente como UUID
- Estados de envío siguen flujo controlado
- CORS configurado para localhost:5173
- Swagger disponible automáticamente
- Tablas se crean automáticamente con JPA
    "estado": "EN_RUTA"
  }
]
```

### 4. Actualizar Estado de Envío

```http
PUT /api/envios/{id}/estado?estado=EN_RUTA
```

**Respuesta (200 OK):**
```json
{
  "id": "envio-001",
  "pedido_id": 1,
  "transportista_id": "550e8400-e29b-41d4-a716-446655440000",
  "direccion_entrega": "Calle Principal 123",
  "estado": "EN_RUTA"
}
```

## 🚀 Ejecución

### Ejecutar Localmente

```bash
cd ms-envios
./mvnw spring-boot:run
```

### Compilar

```bash
cd ms-envios
./mvnw clean package -DskipTests
```

### Ejecutar Tests

```bash
cd ms-envios
./mvnw test
```

## 🧪 Tests Incluidos

### EnvioServiceImplTest
- ✅ `registrarEnvio_exitoso()` - Registra un envío correctamente
- ✅ `registrarEnvio_pedidoIdInvalido_lanzaExcepcion()` - Valida pedidoId
- ✅ `cambiarEstado_exitoso()` - Cambia estado de envío
- ✅ `cambiarEstado_envioNoExiste_lanzaExcepcion()` - Valida existencia

### EnvioControllerTest
- ✅ `registrarEnvio_retorna201()` - HTTP 201 al registrar
- ✅ `listarEnvios_retorna200()` - HTTP 200 al listar

## 🔗 Integración con Otros Servicios

### ms-pedidos
- Consulta ms-envios para seguimiento de envíos de pedidos.

### ms-transportistas
- ms-envios registra transportistas asignados.
- Los transportistas son consultados de ms-transportistas.

### Base de Datos Compartida
- Base de datos: `smartlogix`
- Tabla: `envios`
- Usuario: `root` / Contraseña: `root`

## 🛡️ Validaciones

### En el Modelo (Anotaciones Jakarta Validation)

```java
@NotNull(message = "El pedidoId no puede ser nulo")
private Long pedidoId;

@NotNull(message = "El transportistaId no puede ser nulo")
@NotBlank(message = "El transportistaId no puede estar vacío")
private String transportistaId;

@NotNull(message = "La dirección de entrega no puede ser nula")
@NotBlank(message = "La dirección de entrega no puede estar vacía")
private String direccionEntrega;
```

### En el Servicio

- Validación de pedidoId > 0
- Validación de transportistaId no vacío
- Validación de dirección no vacía
- Búsqueda de envío por ID (lanza `IllegalArgumentException` si no existe)

## 📋 Configuración

### application.properties

```properties
spring.application.name=ms-envios
server.port=8084

# Base de datos compartida
spring.datasource.url=jdbc:mysql://localhost:3306/smartlogix?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

## 📚 Dependencias Principales

```xml
<!-- Spring Boot Web -->
<spring-boot-starter-web>

<!-- Spring Data JPA -->
<spring-boot-starter-data-jpa>

<!-- MySQL Driver -->
<mysql-connector-j>

<!-- Validación -->
<spring-boot-starter-validation>

<!-- Security -->
<spring-boot-starter-security>

<!-- Documentación API -->
<springdoc-openapi-starter-webmvc-ui> (v2.3.0)

<!-- Tests -->
<spring-boot-starter-test>
```

## 🔍 Documentación API (Swagger)

```
http://localhost:8084/swagger-ui.html
```

## ❓ Soporte

Para más información sobre la arquitectura general, consulta el [README.md](../README.md) del proyecto.
