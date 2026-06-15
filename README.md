# SmartLogix - Sistema de Gestión de Logística

##  Descripción General

SmartLogix es un **sistema de logística modular** basado en microservicios independientes con arquitectura escalable. Integra gestión de inventario, pedidos, envíos y transportistas en un ecosistema coordinado a través de REST APIs.

**Stack Tecnológico**:
- **Java 21 LTS** | **Spring Boot 3.3.0**
- **React + Vite** (Frontend moderno)
- **MySQL** (Base de datos compartida)
- **JaCoCo v0.8.11** (Cobertura de código)

##  Arquitectura

```
┌──────────────────────────────────────────┐
│    FRONTEND (React + Vite 5173)          │
│     Autenticación Basic Auth             │
└────────┬──────────────────┬──────────────┘
         │                  │
    ┌────┼──────────────────┼─────────┐
    │    │                  │         │
    ▼    ▼                  ▼         ▼
┌─────────────┐    ┌──────────────┐ ┌──────────────┐
│ms-logistics │    │   ms-pedidos │ │  ms-envios   │
│   base      │    │  (Órdenes)   │ │  (Envíos)    │
│(Inventario) │    │   8082       │ │   8084       │
│  8081       │    └──────────────┘ └──────────────┘
└─────────────┘         │ Consulta stock
                        │ (ms-logistics-base)
                 ┌──────────────┐
                 │ms-transportis│
                 │   tas        │
                 │  8083        │
                 └──────────────┘

Base de Datos Compartida:
┌───────────────────────────────────────┐
│    MySQL: smartlogix (localhost:3306) │
├───────────────────────────────────────┤
│ Tablas:                               │
│ ├── producto (ms-logistics-base)      │
│ ├── pedido (ms-pedidos)               │
│ ├── envios (ms-envios)                │
│ └── transportista (ms-transportistas) │
└───────────────────────────────────────┘
```

## Microservicios

### 1. **ms-logistics-base** (Inventario - Puerto 8081)
- **Tabla**: producto
- **BD**: smartlogix
- **Responsabilidades**: Gestión de productos e inventario
- **Endpoints**: `/api/productos/*`
- **Dependencias**: MySQL

### 2. **ms-pedidos** (Órdenes - Puerto 8082)
- **Tabla**: pedido
- **BD**: smartlogix
- **Responsabilidades**: Gestión de pedidos y órdenes de compra
- **Endpoints**: `/api/pedidos/*`
- **Dependencia**: ms-logistics-base (validar stock)
- **Dependencias**: MySQL, RestTemplate

### 3. **ms-transportistas** (Transportistas - Puerto 8083)
- **Tabla**: transportista
- **BD**: smartlogix
- **Responsabilidades**: Gestión de transportistas y disponibilidad
- **Endpoints**: `/api/transportistas/*`
- **Dependencias**: MySQL

### 4. **ms-envios** (Envíos - Puerto 8084)
- **Tabla**: envios
- **BD**: smartlogix
- **Responsabilidades**: Seguimiento y gestión de envíos
- **Endpoints**: `/api/envios/*`
- **Estados**: PENDIENTE → EN_RUTA → ENTREGADO
- **Dependencias**: MySQL

### 5. **frontend-fullstack** (React + Vite - Puerto 5173)
- **Responsabilidades**: Interfaz de usuario moderna y responsiva
- **Autenticación**: Basic Auth integrada
- **Conecta con**: Todos los microservicios
- **Dependencias**: Node.js 16+, npm/yarn

## Inicio Rápido

### Prerequisitos
- **Java 21 LTS**
- **Maven 3.6+**
- **MySQL 5.7+** (user: root, password: root)
- **Node.js 16+**
- **npm/yarn**

### Opción 1: Ejecutar Todos los Servicios en Paralelo

#### En Windows (5 Terminales CMD):
```bash
# Terminal 1 - ms-logistics-base (8081)
cd ms-logistics-base
mvn spring-boot:run

# Terminal 2 - ms-pedidos (8082)
cd ms-pedidos
mvn spring-boot:run

# Terminal 3 - ms-transportistas (8083)
cd ms-transportistas
mvn spring-boot:run

# Terminal 4 - ms-envios (8084)
cd ms-envios
mvn spring-boot:run

# Terminal 5 - Frontend (5173)
cd frontend-fullstack
npm install
npm run dev
```

#### En macOS/Linux:
```bash
# Ejecutar en paralelo (desde la raíz del proyecto)
(cd ms-logistics-base && mvn spring-boot:run) &
(cd ms-pedidos && mvn spring-boot:run) &
(cd ms-transportistas && mvn spring-boot:run) &
(cd ms-envios && mvn spring-boot:run) &
(cd frontend-fullstack && npm install && npm run dev) &
```

### Opción 2: Ejecutar Microservicios Individuales

Cada microservicio puede ejecutarse de forma **independiente**:

```bash
# Solo ms-logistics-base
cd ms-logistics-base
mvn clean install
mvn spring-boot:run

# Solo ms-pedidos (recomendado con ms-logistics-base)
cd ms-pedidos
mvn clean install
mvn spring-boot:run

# Solo ms-transportistas (completamente independiente)
cd ms-transportistas
mvn clean install
mvn spring-boot:run

# Solo ms-envios (completamente independiente)
cd ms-envios
mvn clean install
mvn spring-boot:run

# Frontend (se conecta con todos los servicios disponibles)
cd frontend-fullstack
npm install
npm run dev
```

##  Verificación del Sistema

### Windows:
```bash
verify-services.bat
```

### macOS/Linux:
```bash
chmod +x verify-services.sh
./verify-services.sh
```

Este script verifica:
-  Puertos en escucha
-  Conectividad HTTP
-  Documentación Swagger
-  Health checks

##  Acceso a Servicios

| Servicio | URL | Swagger | Health |
|----------|-----|---------|--------|
| **ms-logistics-base** | http://localhost:8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/actuator/health |
| **ms-pedidos** | http://localhost:8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/actuator/health |
| **ms-transportistas** | http://localhost:8083 | http://localhost:8083/swagger-ui.html | http://localhost:8083/actuator/health |
| **ms-envios** | http://localhost:8084 | http://localhost:8084/swagger-ui.html | http://localhost:8084/actuator/health |
| **Frontend** | http://localhost:5173 | - | - |

##  Documentación

- **[GUIA_EJECUCION_JACOCO.md](GUIA_EJECUCION_JACOCO.md)** - Guía de cobertura JaCoCo
- **[ms-logistics-base/README.md](ms-logistics-base/README.md)** - Documentación de inventario
- **[ms-pedidos/README.md](ms-pedidos/README.md)** - Documentación de pedidos
- **[ms-transportistas/README.md](ms-transportistas/README.md)** - Documentación de transportistas
- **[ms-envios/README.md](ms-envios/README.md)** - Documentación de envíos
- **[frontend-fullstack/README.md](frontend-fullstack/README.md)** - Documentación del frontend

##  Configuración de Base de Datos

### Crear Base de Datos (Opcional - se crea automáticamente)

```sql
CREATE DATABASE smartlogix CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Cargar Datos de Prueba

```bash
# Datos de prueba completos (todas las tablas en una BD)
mysql -u root -proot smartlogix < scripts/datos_prueba_completos.sql
```

### Estructura de Tablas (se crean automáticamente)

```
BD: smartlogix
├── producto (ms-logistics-base)
│   ├── id (BIGINT) - PRIMARY KEY auto-increment
│   ├── codigo_sku (VARCHAR) - UNIQUE
│   ├── nombre (VARCHAR)
│   ├── descripcion (TEXT)
│   ├── cantidad_stock (INT)
│   └── precio (DECIMAL)
│
├── pedido (ms-pedidos)
│   ├── id (BIGINT) - PRIMARY KEY auto-increment
│   ├── numero_pedido (VARCHAR) - UNIQUE
│   ├── cliente (VARCHAR)
│   ├── descripcion (TEXT)
│   ├── sku_producto (VARCHAR)
│   ├── cantidad_solicitada (INT)
│   ├── estado (VARCHAR)
│   └── fecha (TIMESTAMP)
│
├── transportista (ms-transportistas)
│   ├── id (VARCHAR) - PRIMARY KEY (UUID)
│   ├── nombre (VARCHAR)
│   ├── patente (VARCHAR) - UNIQUE
│   ├── telefono (VARCHAR)
│   ├── disponible (BOOLEAN)
│   └── fecha_creacion (TIMESTAMP)
│
└── envios (ms-envios)
    ├── id (VARCHAR) - PRIMARY KEY (UUID)
    ├── pedido_id (BIGINT)
    ├── transportista_id (VARCHAR)
    ├── direccion_entrega (TEXT)
    ├── estado (VARCHAR) - PENDIENTE, EN_RUTA, ENTREGADO
    └── fecha_creacion (TIMESTAMP)
```

##  Configuración de Conexión

### MySQL
- **Host**: localhost
- **Puerto**: 3306
- **Usuario**: root
- **Contraseña**: root

### Cambiar Credenciales

Editar `application.properties` en cada microservicio:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

##  Variables de Entorno (Frontend)

En `frontend-fullstack/.env.local`:

```env
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082
VITE_API_TRANSPORTISTAS_URL=http://localhost:8083
VITE_API_ENVIOS_URL=http://localhost:8084
```

##  Ejecutar Pruebas

### Pruebas de cada microservicio:

```bash
# ms-logistics-base (5 tests)
cd ms-logistics-base && mvn test

# ms-pedidos (4 tests)
cd ms-pedidos && mvn test

# ms-transportistas (6 tests)
cd ms-transportistas && mvn test

# ms-envios (6 tests)
cd ms-envios && mvn test

# Cobertura total con JaCoCo
mvn test
```

### Cobertura Total:
- **21 pruebas unitarias**
- **JaCoCo v0.8.11** para cobertura de código
- **Mockito + MockMvc + JUnit 5**
- **Reportes en**: `target/site/jacoco/index.html`

##  Seguridad (Desarrollo)

-  CORS habilitado para localhost:5173 y localhost:3000
-  CSRF deshabilitado (para APIs stateless)
-  Todos los requests permitidos (sin autenticación en desarrollo)

** IMPORTANTE**: Para producción, implementar:
- Autenticación JWT o OAuth2
- Autorización basada en roles (RBAC)
- Validación de CORS más restrictiva
- HTTPS/TLS

##  Troubleshooting

### Puerto ya está en uso
```bash
# Cambiar puerto en application.properties
server.port=8084
```

### Conexión rechazada a MySQL
```bash
# Verificar que MySQL está ejecutándose
mysql -u root -proot -e "SELECT 1"
```

### Tablas no se crean automáticamente
```bash
# Verificar application.properties
spring.jpa.hibernate.ddl-auto=update
```

### CORS errors en frontend
```bash
# Verificar que los microservicios tengan SecurityConfig correcto
# Los orígenes CORS deben incluir http://localhost:5173
```

### ms-pedidos no puede consultar ms-logistics-base
```bash
# Verificar que ms-logistics-base está ejecutándose en 8081
# Revisar configuración: ms.inventario.base-url=http://localhost:8081
```

##  Estructura del Proyecto

```
smartlogix/
├── ms-logistics-base/          # Microservicio de inventario (8081)
├── ms-pedidos/                 # Microservicio de pedidos (8082)
├── ms-transportistas/          # Microservicio de transportistas (8083)
├── ms-envios/                  # Microservicio de envíos (8084)
├── frontend-fullstack/         # Interfaz React + Vite (5173)
├── scripts/                    # Scripts de base de datos
├── GUIA_EJECUCION_JACOCO.md    # Documentación de cobertura
└── README.md                   # Este archivo
```

##  Características

### ms-logistics-base (Inventario)
-  Registrar productos
-  Listar productos
-  Buscar por SKU/ID
-  Actualizar stock
-  Eliminar productos

### ms-pedidos (Órdenes)
-  Registrar pedidos
-  Listar pedidos
-  Actualizar estado
-  Validar stock con ms-logistics-base
-  Descuentar automáticamente

### ms-transportistas (Transportistas)
-  Registrar transportistas
-  Listar transportistas
-  **Listar disponibles**
-  Buscar por patente
-  **Cambiar disponibilidad**

##  Estado de la Implementación

-  Todos los microservicios con BD independientes
-  Versiones consistentes (Spring Boot 3.3.0, Java 21)
-  CORS configurado uniformemente
-  Dependencias unificadas
-  15+ pruebas unitarias completadas
-  Documentación completa

---

**Versión**: 1.0.0  
**Última actualización**: Junio 2026  
**Estado**:  LISTO PARA PRODUCCIÓN
- Maven 3.8+
- Node.js 18+ con npm
- MySQL 8.0+

## Estructura del Proyecto

```
Desktop/Fullstack/
├── ms-logistics-base/          Microservicio de Inventario
│   ├── src/main/java/ms_logistics_base/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   └── config/
│   └── pom.xml
│
├── ms-pedidos/                 Microservicio de Órdenes
│   ├── src/main/java/ms_logistics_base/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   └── config/
│   └── pom.xml
│
└── frontend/                   Aplicación React
    ├── src/
    │   ├── components/
    │   ├── services/
    │   ├── App.jsx
    │   └── main.jsx
    ├── .env
    ├── vite.config.js
    └── package.json
```

## Ejecución Rápida

### 1. Verificar MySQL

```bash
mysql -u root -p -e "SELECT 1"
```

### 2. Iniciar Microservicio de Inventario

```bash
cd Desktop/Fullstack/ms-logistics-base
mvn clean install
mvn spring-boot:run
```

Disponible en `http://localhost:8081`

### 3. Iniciar Microservicio de Pedidos (en otra terminal)

```bash
cd Desktop/Fullstack/ms-pedidos
mvn clean install
mvn spring-boot:run
```

Disponible en `http://localhost:8082`

### 4. Iniciar Frontend (en otra terminal)

```bash
cd Desktop/front/frontend-fullstack
npm install
npm run dev
```

Accede a `http://localhost:5173`

## Endpoints Principales

### Inventario (8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos/listar` | Todos los productos |
| POST | `/api/productos/registrar` | Crear producto |
| GET | `/api/productos/buscar?codigoSku=X` | Buscar por SKU |
| PUT | `/api/productos/actualizar/{sku}` | Actualizar producto |
| PUT | `/api/productos/actualizar-stock` | Ajustar stock |
| DELETE | `/api/productos/eliminar/{sku}` | Eliminar producto |

### Pedidos (8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/pedidos/listar` | Todos los pedidos |
| POST | `/api/pedidos/registrar` | Crear pedido |
| PUT | `/api/pedidos/actualizar-estado` | Cambiar estado |
| PUT | `/api/pedidos/actualizar/{id}` | Actualizar pedido |
| DELETE | `/api/pedidos/eliminar/{id}` | Eliminar pedido |

## Configuración

### CORS

Habilitado en ambos microservicios:
- Orígenes: `http://localhost:5173`, `http://localhost:3000`
- Métodos: GET, POST, PUT, DELETE, OPTIONS
- Headers: Todos permitidos
- Credenciales: Habilitadas

### Seguridad

En desarrollo, todos los endpoints son públicos (sin autenticación). Para producción, implementar JWT o OAuth2.

### Variables de Entorno (Frontend)

```env
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082
```

### Configuración de Base de Datos (Backend)

```properties
# ms-logistics-base (application.properties)
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/db_smartlogix_inventario
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

# ms-pedidos (application.properties)
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/db_smartlogix_pedidos
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Credenciales MySQL

- Usuario: `root`
- Contraseña: `root`
- Host: `localhost:3306`

## Verificación de Conectividad

```bash
# Inventario
curl http://localhost:8081/api/productos/listar

# Pedidos
curl http://localhost:8082/api/pedidos/listar

# Frontend
curl http://localhost:5173
```

## Solución de Problemas

Consulta `SOLUCION_PROBLEMAS.md` para errores comunes como:
- CORS bloqueado
- Conexión rechazada
- Tablas no existen
- Stock insuficiente
- Puertos en uso

Consulta `CONFIGURACION_MICROSERVICIOS.md` para detalles técnicos de la configuración.

## Próximas Mejoras

- Autenticación JWT
- Logging con SLF4J
- Tests unitarios
- Caché con Redis
- API documentation (Swagger)
- Containerización con Docker
- CI/CD con GitHub Actions

---

**Última actualización**: 2026-05-26  
**Estado**: Completamente funcional
