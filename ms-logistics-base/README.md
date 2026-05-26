# SmartLogix - Microservicio de Inventario (Puerto 8081)

Módulo independiente especializado en control de existencias, catalogación de productos y movimientos físicos en bodega dentro del ecosistema SmartLogix.

## Stack y Arquitectura

Implementado con Java 17 y Spring Boot 4 en un enfoque tradicional: código fuente puro, constructores y getters/setters escritos manualmente para máxima transparencia. 

Las validaciones de negocio (stock y precio no negativos) se aplican a nivel de servicio mediante condicionales tradicionales, garantizando que el sistema rechace operaciones que comprometan la integridad de datos. La persistencia corre sobre MySQL (`db_smartlogix_inventario`) mediante Spring Data JPA, con migración automática de esquema.

## Endpoints REST

### Registrar Producto
```http
POST /api/productos/registrar
```
Solicitud:
```json
{
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```
Validaciones: precio ≥ 0, stock inicial ≥ 0

Respuesta (201 Created):
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```

### Listar Productos
```http
GET http://localhost:8081/api/productos/listar
```
Recupera el catálogo completo de inventario.

Respuesta (200 OK):
```json
[
  {
    "id": 1,
    "codigoSku": "SKU-001",
    "nombre": "Producto Ejemplo",
    "descripcion": "Descripción del producto",
    "cantidadStock": 100,
    "precio": 29.99
  }
]
```

### Buscar Producto por SKU
```http
GET http://localhost:8081/api/productos/buscar?codigoSku=PROD-001
```
Parámetro: `codigoSku` (requerido)

Respuesta (200 OK):
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```

### Actualizar Stock
```http
PUT http://localhost:8081/api/productos/actualizar-stock?codigoSku=PROD-001&cantidad=10
```
Incrementa o disminuye existencias. La operación se rechaza si genera inventario negativo.

Parámetros: `codigoSku`, `cantidad` (positivo para agregar, negativo para restar)

Validación: stock resultante ≥ 0

Respuesta (200 OK):
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 110,
  "precio": 29.99
}
```

## Modelo de Datos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único (generado automáticamente) |
| `codigoSku` | String | Código SKU único del producto |
| `nombre` | String | Nombre del producto |
| `descripcion` | String | Descripción detallada |
| `cantidadStock` | Integer | Cantidad disponible en bodega |
| `precio` | Double | Precio unitario |

## Instalación y Configuración

### Requisitos
- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### Setup

Clonar y navegar:
```bash
git clone <url-repo>
cd ms-logistics-base
```

Crear la base de datos:
```sql
CREATE DATABASE db_smartlogix_inventario;
```

Compilar y ejecutar:
```bash
mvn clean package
mvn spring-boot:run
```

El servicio estará disponible en `http://localhost:8081`

### Configuración (application.properties)
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/db_smartlogix_inventario
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Estructura del Proyecto

```
ms-logistics-base/
├── src/main/java/ms_logistics_base/
│   ├── MsLogisticsBaseApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── ProductoController.java
│   ├── service/
│   │   ├── ProductoService.java
│   │   └── ProductoServiceImpl.java
│   ├── repository/
│   │   └── ProductoRepository.java
│   └── model/
│       └── Producto.java
├── src/main/resources/
│   └── application.properties
├── src/test/
│   └── java/ms_logistics_base/
├── pom.xml
└── README.md
```

## Notas

- Spring Security configurado con `permitAll()` para propósitos de evaluación.
- Las validaciones de negocio se aplican a nivel de servicio.
- Los endpoints siguen convenciones REST estándar.
- La base de datos se configura automáticamente mediante Spring Data JPA.

---

**Versión:** 1.0.0  
**Última actualización:** 2026-05-24  
**Estrategia de Ramificación:** Trunk-Based Development


### 1. Registrar Producto
```http
POST /api/productos/registrar
```
Registra un producto en el sistema, validando que el precio y stock inicial no sean menores a cero.

**Cuerpo de la solicitud (JSON):**
```json
{
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```

**Validaciones:**
- Precio >= 0
- Stock inicial >= 0

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```

---

### 2. Listar Productos
```http
GET http://localhost:8081/api/productos/listar
```
Obtiene la lista completa del catálogo de inventario en bodega.

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "codigoSku": "SKU-001",
    "nombre": "Producto Ejemplo",
    "descripcion": "Descripción del producto",
    "cantidadStock": 100,
    "precio": 29.99
  }
]
```

---

### 3. Buscar Producto por SKU
```http
GET http://localhost:8081/api/productos/buscar?codigoSku=PROD-001
```
Recupera los detalles de un artículo usando su código SKU único.

**Parámetro:**
- `codigoSku` - Código SKU del producto (requerido)

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 100,
  "precio": 29.99
}
```

---

### 4. Actualizar Stock
```http
PUT http://localhost:8081/api/productos/actualizar-stock?codigoSku=PROD-001&cantidad=10
```
Incrementa o disminuye el stock disponible. Lanza excepciones si la operación pretende dejar las existencias en números negativos.

**Parámetros Query:**
- `codigoSku` - Código SKU del producto (requerido)
- `cantidad` - Cantidad a agregar (positivo) o restar (negativo) (requerido)

**Validaciones:**
- Stock resultante >= 0
- La transacción se rechaza si genera inventario negativo

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "codigoSku": "SKU-001",
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "cantidadStock": 110,
  "precio": 29.99
}
```

---

## 📋 Estructura del Modelo de Datos

### Entidad: Producto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único (generado automáticamente) |
| `codigoSku` | String | Código SKU único del producto |
| `nombre` | String | Nombre del producto |
| `descripcion` | String | Descripción detallada |
| `cantidadStock` | Integer | Cantidad disponible en bodega |
| `precio` | Double | Precio unitario |

---

## 🔧 Configuración Inicial

### Requisitos Previos
- JDK 17 o superior
- MySQL 8.0+
- Maven 3.8+

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone <url-repo>
   cd ms-logistics-base
   ```

2. **Configurar la base de datos:**
   Crear la base de datos MySQL:
   ```sql
   CREATE DATABASE db_smartlogix_inventario;
   ```

3. **Compilar el proyecto:**
   ```bash
   mvn clean package
   ```

4. **Ejecutar el microservicio:**
   ```bash
   mvn spring-boot:run
   ```

El servicio estará disponible en: `http://localhost:8081`

---

## ⚙️ Configuración de la Aplicación

### application.properties
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/db_smartlogix_inventario
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```


---

## 📦 Estructura del Proyecto

```
ms-logistics-base/
├── src/
│   ├── main/
│   │   ├── java/ms_logistics_base/
│   │   │   ├── MsLogisticsBaseApplication.java    (Punto de entrada)
│   │   │   ├── config/
│   │   │   │   └── SeguridadConfig.java            (Configuración de Spring Security)
│   │   │   ├── controller/
│   │   │   │   └── ProductoController.java         (Endpoints REST)
│   │   │   ├── service/
│   │   │   │   ├── ProductoService.java            (Interfaz del servicio)
│   │   │   │   └── ProductoServiceImpl.java         (Implementación del servicio)
│   │   │   ├── repository/
│   │   │   │   └── ProductoRepository.java         (Acceso a datos JPA)
│   │   │   └── model/
│   │   │       └── Producto.java                   (Entidad JPA)
│   │   └── resources/
│   │       └── application.properties              (Configuración de la aplicación)
│   └── test/
│       └── java/ms_logistics_base/
│           └── MsLogisticsBaseApplicationTests.java
├── pom.xml                                          (Dependencias Maven)
└── README.md                                        (Este archivo)
```

---

**Versión:** 1.0.0  
**Última actualización:** 2026-05-24  
**Estrategia de Ramificación:** Trunk-Based Development
