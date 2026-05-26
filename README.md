# SmartLogix - Sistema de Gestión de Inventario y Pedidos

SmartLogix es una aplicación de microservicios para la gestión integral de logistica.

## Tecnología

- **Backend**: Spring Boot 4.0.6 con Java 17
- **Frontend**: React 19 + Vite
- **Base de Datos**: MySQL con dos esquemas independientes
- **Comunicación**: REST API con CORS habilitado

## Arquitectura

```
Frontend (React - Puerto 5173)
         │
    ┌────┴────┐
    │          │
    ▼          ▼
Inventario   Pedidos
(Puerto      (Puerto
 8081)        8082)
    │          │
    ▼          ▼
db_smartlogix_inventario  db_smartlogix_pedidos
```

## Requisitos Previos

- Java 17+
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

En desarrollo, todos los endpoints son públicos (sin autenticación).

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
