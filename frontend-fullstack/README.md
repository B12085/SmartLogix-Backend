# 📦 SmartLogix - Sistema de Gestión de Inventario y Pedidos

Frontend en React + Vite que integra dos microservicios de Spring Boot con autenticación Basic Auth.

## 🚀 Características

- ✅ Gestión de Inventario (CRUD de productos)
- ✅ Gestión de Pedidos (crear y listar pedidos)
- ✅ Sincronización automática de stock entre microservicios
- ✅ Autenticación Basic Auth integrada
- ✅ Interfaz moderna y responsive
- ✅ Manejo centralizado de errores

## 📋 Requisitos Previos

Asegúrate de tener corriendo:
- **Node.js 16+**
- **Microservicio de Inventario**: http://localhost:8081 (Spring Boot)
- **Microservicio de Pedidos**: http://localhost:8082 (Spring Boot)
- **Spring Security activado** en ambos microservicios

## 🔧 Configuración Inicial

### 1. Instalar dependencias

```bash
npm install
```

### 2. Configurar variables de entorno

Copia el archivo `.env.example` a `.env` y actualiza las credenciales:

```bash
cp .env.example .env
```

Contenido de `.env`:
```env
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082
VITE_API_USERNAME=user
VITE_API_PASSWORD=password
```

> ⚠️ El archivo `.env` está en `.gitignore` - nunca lo commitees con credenciales reales.

### 3. Iniciar el servidor de desarrollo

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

## 📂 Estructura del Proyecto

```
src/
├── services/
│   └── apiService.js          ← 🔐 Lógica de autenticación Basic Auth
├── App.jsx                    ← Componente principal
├── App.css
├── main.jsx
├── index.css
└── assets/

.env                           ← Variables de entorno (NO commitir)
.env.example                   ← Plantilla de ejemplo
AUTENTICACION_BASIC_AUTH.md    ← Documentación de autenticación
EJEMPLOS_EXTENSION_API.md      ← Ejemplos de extensión del servicio
```

## 🔐 Autenticación Basic Auth

### ¿Cómo funciona?

El proyecto utiliza **Basic Auth** para autenticarse con los microservicios:

1. Las credenciales (`usuario:contraseña`) se codifican en Base64
2. Se envían en el header HTTP: `Authorization: Basic <base64>`
3. Los microservicios Spring Boot validan las credenciales

**Ejemplo de header generado:**
```
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

### Servicio Centralizado

Todas las peticiones HTTP van a través de `src/services/apiService.js`:

```javascript
import { inventarioService, pedidosService } from './services/apiService'

// Las credenciales se añaden automáticamente
const productos = await inventarioService.listarProductos()
```

## 📖 Uso del Servicio API

### Productos (Inventario)

```javascript
import { inventarioService } from './services/apiService'

// Listar todos los productos
const productos = await inventarioService.listarProductos()

// Registrar nuevo producto
await inventarioService.registrarProducto({
  sku: 'PROD-001',
  nombre: 'Laptop Dell',
  descripcion: 'Laptop i7',
  cantidadStock: 10,
  precio: 999.99
})

// Actualizar producto
await inventarioService.actualizarProducto('PROD-001', {
  nombre: 'Laptop Dell XPS',
  precio: 1099.99
})

// Eliminar producto
await inventarioService.eliminarProducto('PROD-001')

// Actualizar stock
await inventarioService.actualizarStock('PROD-001', -5) // Restar 5 unidades
```

### Pedidos

```javascript
import { pedidosService } from './services/apiService'

// Listar todos los pedidos
const pedidos = await pedidosService.listarPedidos()

// Registrar nuevo pedido
await pedidosService.registrarPedido({
  numeroPedido: 'PED-001',
  cliente: 'Cliente X',
  descripcion: 'Pedido urgente',
  skuProducto: 'PROD-001',
  cantidadSolicitada: 3
})
```

## 🧪 Pruebas de la API

### Usando cURL

```bash
# Listar productos con autenticación
curl -u user:password http://localhost:8081/api/productos/listar

# Registrar producto
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'user:password' | base64)" \
  -d '{"sku":"PROD-001","nombre":"Laptop","precio":999.99}' \
  http://localhost:8081/api/productos/registrar
```

### Usando Postman

1. En la pestaña **Authorization**, selecciona **Basic Auth**
2. Ingresa las credenciales: `user` / `password`
3. Postman automáticamente genera el header
4. Realiza tus requests

## ❌ Solución de Problemas

### Error 401 Unauthorized

**Posibles causas:**
- Credenciales incorrectas en `.env`
- Microservicio requiere usuario diferente
- Header Authorization no se está enviando

**Solución:**
```javascript
// Verificar en DevTools > Network > Headers
// Debe existir: Authorization: Basic dXNlcjpwYXNzd29yZA==

// O debuguear en la consola:
console.log(import.meta.env.VITE_API_USERNAME)
console.log(import.meta.env.VITE_API_PASSWORD)
```

### Error 503 Service Unavailable

**Posible causa:**
- Microservicios no están corriendo en localhost:8081 o 8081

**Solución:**
```bash
# Verificar que los microservicios estén activos
curl http://localhost:8081/api/productos/listar
curl http://localhost:8082/api/pedidos/listar
```

## 📦 Scripts Disponibles

```bash
# Servidor de desarrollo
npm run dev

# Build para producción
npm run build

# Previsualizar build
npm run preview

# Linting
npm run lint
```

## 🔗 Documentación Completa

- [AUTENTICACION_BASIC_AUTH.md](./AUTENTICACION_BASIC_AUTH.md) - Guía detallada de autenticación
- [EJEMPLOS_EXTENSION_API.md](./EJEMPLOS_EXTENSION_API.md) - Ejemplos de extensión del servicio

## 🚀 Pasos Siguientes

1. Testea los endpoints con Postman o cURL
2. Verifica los headers de autenticación en DevTools
3. Si todo funciona, puedes extender los servicios con nuevos endpoints
4. Para producción, usa HTTPS y JWT en lugar de Basic Auth

## 📝 Buenas Prácticas de Seguridad

✅ **HACER:**
- Usar variables de entorno para credenciales
- HTTPS en producción
- Rotar credenciales regularmente
- Logs de auditoría en servidor

❌ **NO HACER:**
- Hardcodear credenciales en el código
- Commitear `.env` a Git
- Basic Auth sin HTTPS
- Exponer tokens en URLs

## 🤝 Soporte

Si tienes problemas:
1. Revisa los logs en DevTools (F12)
2. Verifica que los microservicios están corriendo
3. Comprueba las credenciales en `.env`
4. Consulta [AUTENTICACION_BASIC_AUTH.md](./AUTENTICACION_BASIC_AUTH.md)

---

**Hecho con ❤️ por tu equipo de desarrollo**

