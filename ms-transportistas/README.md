# ms-transportistas - Microservicio de Transportistas

Microservicio REST para la gestión de transportistas, disponibilidad y asignación de envíos en SmartLogix.

## 📋 Especificaciones Técnicas

| Propiedad | Valor |
|-----------|-------|
| **Puerto** | 8083 |
| **Spring Boot** | 3.3.0 |
| **Java** | 21 LTS |
| **Base de Datos** | MySQL (smartlogix) |
| **Tabla Principal** | transportista |
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
- Validaciones de datos de transportista
- Estados de disponibilidad (disponible/no disponible)
- Identificadores único por patente
- Migrations automáticas de esquema

## 🔌 Endpoints REST

### Registrar Transportista
```http
POST /api/transportistas/registrar
Content-Type: application/json
```

**Solicitud**:
```json
{
  "nombre": "Juan Pérez",
  "patente": "ABC-1234",
  "telefono": "1234567890",
  "disponible": true
}
```

**Validaciones**:
- nombre no vacío
- patente única
- telefono requerido
- disponible boolean

**Respuesta (201 Created)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "patente": "ABC-1234",
  "telefono": "1234567890",
  "disponible": true,
  "fechaCreacion": "2025-06-15T10:30:00"
}
```

### Listar Transportistas
```http
GET /api/transportistas/listar
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Juan Pérez",
    "patente": "ABC-1234",
    "telefono": "1234567890",
    "disponible": true,
    "fechaCreacion": "2025-06-15T10:30:00"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "nombre": "María García",
    "patente": "XYZ-5678",
    "telefono": "0987654321",
    "disponible": false,
    "fechaCreacion": "2025-06-15T11:00:00"
  }
]
```

### Listar Transportistas Disponibles
```http
GET /api/transportistas/disponibles
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Juan Pérez",
    "patente": "ABC-1234",
    "telefono": "1234567890",
    "disponible": true,
    "fechaCreacion": "2025-06-15T10:30:00"
  }
]
```

### Cambiar Disponibilidad
```http
PUT /api/transportistas/{id}/disponibilidad?disponible=false
```

**Parámetros de ruta**:
- `id` (requerido): ID del transportista (UUID)

**Parámetros de query**:
- `disponible` (requerido): true o false

**Respuesta (200 OK)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "patente": "ABC-1234",
  "telefono": "1234567890",
  "disponible": false,
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
1. Registrar transportista con patente válida (ABC-1234)
2. Listar todos los transportistas
3. Cambiar disponibilidad del transportista
4. Listar transportistas disponibles
5. Validar patente única
6. Validaciones de datos obligatorios

## 🚀 Ejecutar Localmente

```bash
# Instalar dependencias
mvn clean install

# Ejecutar servicio
mvn spring-boot:run
```

El servicio estará disponible en:
- **API**: http://localhost:8083/api/transportistas
- **Swagger**: http://localhost:8083/swagger-ui.html
- **Health**: http://localhost:8083/actuator/health

## 📊 Estructura de Base de Datos

**Tabla: transportista**
```sql
CREATE TABLE transportista (
  id VARCHAR(36) PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  patente VARCHAR(50) UNIQUE NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  disponible BOOLEAN DEFAULT true,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🌐 Integración en SmartLogix

Este microservicio es consumido por:
- **ms-envios** (asignar transportistas a envíos)
- **frontend-fullstack** (listar y gestionar transportistas)

## 📝 Flujo de Uso

1. **Frontend** registra nuevo transportista
2. **ms-envios** consulta transportistas disponibles
3. **Frontend** actualiza disponibilidad del transportista
4. Sistema mantiene historial de cambios

## 📝 Notas

- IDs generados automáticamente como UUID
- Patentes deben ser únicas en el sistema
- CORS configurado para localhost:5173
- Swagger disponible automáticamente
