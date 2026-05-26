# Solución de Problemas - SmartLogix

## CORS Bloqueado

**Síntoma**: `Access to XMLHttpRequest at 'http://localhost:8081/...' from origin 'http://localhost:5173' has been blocked by CORS policy`

**Causa**: CORS no está configurado correctamente

**Solución**:
1. Verificar `SecurityConfig.java` en ambos microservicios
2. Asegurar que incluye `http://localhost:5173` en orígenes permitidos
3. Verificar que tiene `@Configuration` y `WebMvcConfigurer`
4. Reiniciar ambos microservicios

Archivos:
- `/ms-logistics-base/src/main/java/ms_logistics_base/config/SecurityConfig.java`
- `/ms-pedidos/src/main/java/ms_logistics_base/config/SecurityConfig.java`

## Conexión Rechazada

**Síntoma**: `Connection refused: connect` o `Failed to connect to localhost:8081`

**Causa**: Los microservicios no están corriendo

**Solución**:
1. Verificar MySQL está corriendo:
   ```bash
   mysql --version
   mysql -u root -p -e "SELECT 1"
   ```

2. Compilar y ejecutar microservicios:
   ```bash
   cd ms-logistics-base
   mvn clean install
   mvn spring-boot:run
   ```

3. Revisar logs para errores de inicio

## Error 400 Bad Request

**Síntoma**: `HTTP 400: Bad Request` al registrar producto o pedido

**Causa**: Campos requeridos ausentes o validaciones incumplidas

**Solución**:
- Verificar todos los campos requeridos:
  - **Producto**: sku, nombre, precio
  - **Pedido**: numeroPedido, cliente, skuProducto, cantidadSolicitada
- Validaciones:
  - precio > 0
  - cantidadStock >= 0
  - cantidadSolicitada > 0

## Error 404 Not Found

**Síntoma**: `HTTP 404: Not Found`

**Causa**: URL del endpoint es incorrecta

**Solución**:
- Verificar rutas correctas:
  - Inventario: `http://localhost:8081/api/productos/...`
  - Pedidos: `http://localhost:8082/api/pedidos/...`
  - Frontend proxy: `/api/productos/...` y `/api/pedidos/...`

## Tabla no Existe

**Síntoma**: `Table 'db_smartlogix_inventario.productos' doesn't exist`

**Causa**: Hibernate no creó las tablas automáticamente

**Solución**:
1. Verificar `spring.jpa.hibernate.ddl-auto=update` en `application.properties`
2. Eliminar bases de datos:
   ```sql
   DROP DATABASE db_smartlogix_inventario;
   DROP DATABASE db_smartlogix_pedidos;
   ```
3. Reiniciar microservicios (crearán tablas automáticamente)

## Stock Insuficiente

**Síntoma**: `⚠ Pedido registrado, pero FALLO al descontar stock: Stock insuficiente`

**Causa**: Cantidad insuficiente del producto solicitado

**Solución**:
1. Agregar más stock:
   - Ir a "Gestión de Inventario"
   - Buscar el producto
   - Aumentar cantidad disponible

2. O crear pedido con cantidad menor

## Variables de Entorno no se Cargan

**Síntoma**: `VITE_API_INVENTARIO_URL is undefined` en consola

**Causa**: Variables de entorno no se cargan correctamente

**Solución**:
1. Verificar que `.env` existe con contenido:
   ```
   VITE_API_INVENTARIO_URL=http://localhost:8081
   VITE_API_PEDIDOS_URL=http://localhost:8082
   ```

2. Reiniciar servidor dev:
   ```bash
   npm run dev
   ```

3. Verificar en main.jsx que usa `import.meta.env.VITE_*`

## Pedido no Aparece en Lista

**Síntoma**: El pedido se registra pero no aparece en la tabla

**Causa**: Frontend no recarga la lista o hay error en el response

**Solución**:
1. Verificar en DevTools (F12) → Network → respuesta del POST
2. Verificar en consola si hay error
3. Hacer refresh manual (F5)
4. Verificar en base de datos:
   ```sql
   SELECT * FROM db_smartlogix_pedidos.pedidos;
   ```

## Puerto en Uso

**Síntoma**: `Address already in use: bind`

**Causa**: Puerto ya está ocupado

**Solución**:

Para Puerto 8081:
```bash
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

Para Puerto 8082:
```bash
netstat -ano | findstr :8082
taskkill /PID <PID> /F
```

Para Puerto 5173:
```bash
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

## Verificaciones Rápidas

**Conectividad básica**:
```bash
curl http://localhost:8081/api/productos/listar
curl http://localhost:8082/api/pedidos/listar
```

**Base de datos**:
```sql
USE db_smartlogix_inventario;
SHOW TABLES;
SELECT * FROM producto;

USE db_smartlogix_pedidos;
SHOW TABLES;
SELECT * FROM pedidos;
```

**Frontend** (DevTools F12):
- Console: Errores de JavaScript y red
- Network: Requests y responses
- Application: localStorage y variables de entorno

**Backend** (Terminal):
- Ver logs de Spring Boot
- Buscar líneas con ERROR o WARN
- Verificar conexión a base de datos

## Reset Completo

Si nada funciona, hacer reset:

```bash
# 1. Detener todos los servicios (Ctrl+C en cada terminal)

# 2. Limpiar y recompilar
cd ms-logistics-base
mvn clean install

cd ../ms-pedidos
mvn clean install

# 3. Resetear base de datos (MySQL)
DROP DATABASE IF EXISTS db_smartlogix_inventario;
DROP DATABASE IF EXISTS db_smartlogix_pedidos;

# 4. Reiniciar servicios
# Terminal 1
mvn spring-boot:run

# Terminal 2
mvn spring-boot:run

# Terminal 3
npm run dev
```

---

Para más detalles técnicos, consulta `CONFIGURACION_MICROSERVICIOS.md`
