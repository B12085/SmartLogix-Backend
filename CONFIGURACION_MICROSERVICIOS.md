# Configuración de Microservicios - SmartLogix

## Microservicios Implementados

### 1. MS-LOGISTICS-BASE (Inventario)

**Puerto**: 8081  
**Base de datos**: `db_smartlogix_inventario`  
**Tabla**: `producto`

Endpoints:
- `POST /api/productos/registrar`
- `GET /api/productos/listar`
- `GET /api/productos/buscar?codigoSku=XXXX`
- `GET /api/productos/buscar-por-id?id=XXXX`
- `PUT /api/productos/actualizar/{sku}`
- `PUT /api/productos/actualizar-stock?sku=XXXX&cantidad=N`
- `PUT /api/productos/{id}/descontar-stock?cantidad=N`
- `DELETE /api/productos/eliminar/{sku}`

### 2. MS-PEDIDOS (Órdenes)

**Puerto**: 8082  
**Base de datos**: `db_smartlogix_pedidos`  
**Tabla**: `pedidos`

Endpoints:
- `POST /api/pedidos/registrar`
- `GET /api/pedidos/listar`
- `PUT /api/pedidos/actualizar-estado?numeroPedido=XXX&nuevoEstado=PENDIENTE`
- `PUT /api/pedidos/actualizar/{id}`
- `DELETE /api/pedidos/eliminar/{id}`

### 3. Frontend (React + Vite)

**Puerto**: 5173

Variables de entorno:
```env
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082
```

Proxy configurado para ambos microservicios en `vite.config.js`

## Seguridad y CORS

CORS está habilitado en ambos microservicios con:
- Orígenes permitidos: `http://localhost:5173`, `http://localhost:3000`
- Métodos: GET, POST, PUT, DELETE, OPTIONS
- Headers: Todos permitidos
- Credenciales: Habilitadas

Spring Security está configurada para desarrollo local:
- CSRF deshabilitado
- Todos los requests permitidos (sin autenticación requerida)
- Acceso público a todos los endpoints

Para producción, implementar autenticación JWT u OAuth2.

## Modelos de Datos

### Producto (ms-logistics-base)

```
- id: Long (auto-generado)
- sku: String (único, requerido)
- codigoSku: String (alias para sku)
- nombre: String (requerido)
- descripcion: String
- cantidadStock: Integer (>= 0)
- precio: Double (>= 0)
```

Validaciones:
- SKU debe ser único
- Nombre y SKU no pueden ser nulos
- Stock y precio no pueden ser negativos

### Pedido (ms-pedidos)

```
- id: Long (auto-generado)
- numeroPedido: String (único, requerido)
- cliente: String (requerido)
- descripcion: String
- productoId: String (ID del producto en ms-logistics-base)
- skuProducto: String (obtenido de ms-logistics-base)
- cantidadSolicitada: Integer (> 0)
- estado: String (PENDIENTE|PROCESADO|ENVIADO|ENTREGADO)
- fecha: LocalDateTime (auto-generado, timestamp de creación)
```

Validaciones:
- Número de pedido debe ser único
- Cantidad solicitada debe ser mayor a 0
- Estado debe ser uno de los cuatro valores válidos
- El producto debe existir en ms-logistics-base
- Debe haber stock suficiente

## Dependencias Principales

### ms-logistics-base y ms-pedidos

- `spring-boot-starter-web`: Web MVC y REST
- `spring-boot-starter-data-jpa`: Persistencia
- `mysql-connector-java`: Driver MySQL
- `spring-boot-starter-validation`: Validación de datos
- `spring-boot-starter-security`: Seguridad (deshabilitada en dev)
- `spring-boot-starter-webflux`: Soporte reactivo

## Comunicación entre Servicios

El ms-pedidos se comunica con ms-logistics-base mediante `RestTemplate`:

1. **Obtener SKU del producto**:
   ```
   GET http://localhost:8081/api/productos/buscar-por-id?id={productoId}
   ```

2. **Validar stock disponible**:
   ```
   GET http://localhost:8081/api/productos/buscar-por-id?id={productoId}
   ```

3. **Descontar stock (después de registrar pedido)**:
   ```
   PUT http://localhost:8081/api/productos/{id}/descontar-stock?cantidad={cantidad}
   ```

Si el descuento de stock falla, el pedido se revierte de la base de datos.

## Flujo de Creación de Pedido

1. Frontend envía solicitud a ms-pedidos
2. ms-pedidos valida cantidad solicitada > 0
3. ms-pedidos obtiene SKU del producto desde ms-logistics-base
4. ms-pedidos valida que hay stock suficiente
5. ms-pedidos guarda el pedido en su base de datos
6. ms-pedidos intenta descontar stock desde ms-logistics-base
7. Si el descuento falla, ms-pedidos revierte la creación del pedido
8. Frontend recibe respuesta de éxito o error

## Configuración de Base de Datos

Host: `localhost`  
Puerto: `3306`  
Usuario: `root`  
Contraseña: `root`

### Crear bases de datos manualmente (opcional)

```sql
CREATE DATABASE db_smartlogix_inventario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE db_smartlogix_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Las tablas se crean automáticamente cuando Spring Boot inicia (configuración `ddl-auto=update`).

## Cambios Realizados

1. Corregida dependencia `webmvc` → `web` en ms-logistics-base
2. Agregada `RestTemplate` para comunicación inter-servicio
3. Actualizado modelo `Pedido` con campos faltantes
4. Verificado CORS en `SecurityConfig`
5. Agregada configuración `RestTemplateConfig`
6. Actualizado `vite.config.js` con proxy dual
7. Validadas variables de entorno en frontend

## Notas Importantes

- Ambos microservicios usan el mismo package base `ms_logistics_base` (por razones históricas)
- CORS y seguridad están configurados para desarrollo local
- Para producción, actualizar orígenes CORS permitidos y usar variables de entorno para credenciales
- Las tablas se crean automáticamente. Si necesitas recrearlas, elimina las bases de datos y reinicia los microservicios
- ms-pedidos depende de ms-logistics-base para validar productos y gestionar stock

---

Para procedimientos de ejecución, consulta `README.md`  
Para solución de problemas, consulta `SOLUCION_PROBLEMAS.md`
