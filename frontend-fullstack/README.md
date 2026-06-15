# 📦 SmartLogix Frontend - React + Vite

Interfaz de usuario moderna y responsiva para el sistema de gestión de logística SmartLogix. Integra múltiples microservicios de Spring Boot con autenticación Basic Auth.

## 🚀 Características

- ✅ **Gestión de Inventario** - CRUD completo de productos
- ✅ **Gestión de Pedidos** - Crear y listar pedidos con validación de stock
- ✅ **Gestión de Transportistas** - Registrar y cambiar disponibilidad
- ✅ **Seguimiento de Envíos** - Listar y monitorear estado de envíos
- ✅ **Autenticación Basic Auth** - Integrada en todas las peticiones
- ✅ **Interfaz Responsive** - Compatible con dispositivos móviles
- ✅ **Manejo Centralizado de Errores** - Consistencia en toda la app

## 📋 Requisitos Previos

Asegúrate de tener corriendo los siguientes servicios:
- **ms-logistics-base** (Inventario): http://localhost:8081
- **ms-pedidos** (Órdenes): http://localhost:8082
- **ms-transportistas** (Transportistas): http://localhost:8083
- **ms-envios** (Envíos): http://localhost:8084
- **MySQL 5.7+** en localhost:3306 (user: root, pass: root)

## 🔧 Configuración Inicial

### 1. Instalar Dependencias

```bash
npm install
```

### 2. Configurar Variables de Entorno

Copia el archivo `.env.example` a `.env.local` (NO commitir):

```bash
cp .env.example .env.local
```

Contenido de `.env.local`:
```env
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082
VITE_API_TRANSPORTISTAS_URL=http://localhost:8083
VITE_API_ENVIOS_URL=http://localhost:8084
VITE_API_USERNAME=user
VITE_API_PASSWORD=password
```

**⚠️ IMPORTANTE**: El archivo `.env.local` está en `.gitignore` - nunca lo commitees con credenciales reales.

### 3. Iniciar el Servidor de Desarrollo

```bash
npm run dev
```

La aplicación estará disponible en **http://localhost:5173**

## 📂 Estructura del Proyecto

```
frontend-fullstack/
├── public/                      # Assets estáticos
├── src/
│   ├── services/
│   │   └── apiService.js        # 🔐 Lógica de autenticación Basic Auth
│   ├── pages/
│   │   ├── Envios.jsx          # Página de envíos
│   │   └── Transportistas.jsx   # Página de transportistas
│   ├── App.jsx                 # Componente principal
│   ├── App.css
│   ├── main.jsx
│   ├── index.css
│   └── assets/
├── .env.local                  # Variables de entorno (NO commitir)
├── .env.example                # Plantilla de variables
├── vite.config.js              # Configuración de Vite
├── eslint.config.js            # Configuración de ESLint
├── index.html
├── package.json
├── AUTENTICACION_BASIC_AUTH.md # Documentación de autenticación
└── README.md                   # Este archivo
```

## 🔐 Autenticación Basic Auth

### ¿Cómo Funciona?

El proyecto utiliza **Basic Auth** para autenticarse con los microservicios:

1. Las credenciales (`usuario:contraseña`) se codifican en Base64
2. Se envían en el header HTTP: `Authorization: Basic <base64>`
3. Los microservicios Spring Boot validan las credenciales

**Ejemplo de header generado**:
```
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

### Servicio Centralizado

Todas las peticiones HTTP se canalizan a través de `src/services/apiService.js`:

```javascript
import { 
  inventarioService, 
  pedidosService,
  transportistasService,
  enviosService 
} from './services/apiService'

// Las credenciales se añaden automáticamente
const productos = await inventarioService.listarProductos()
const pedidos = await pedidosService.listarPedidos()
const transportistas = await transportistasService.listar()
const envios = await enviosService.listar()
```

## 📖 Uso del Servicio API

### Inventario (ms-logistics-base)

```javascript
// Listar productos
const productos = await inventarioService.listarProductos()

// Buscar por SKU
const producto = await inventarioService.buscarPorSku('SKU-001')

// Registrar producto
const nuevoProducto = await inventarioService.registrar({
  codigoSku: 'SKU-001',
  nombre: 'Laptop',
  descripcion: 'Laptop Dell XPS',
  cantidadStock: 100,
  precio: 1299.99
})

// Actualizar stock
await inventarioService.actualizarStock('SKU-001', 10)
```

### Pedidos (ms-pedidos)

```javascript
// Listar pedidos
const pedidos = await pedidosService.listarPedidos()

// Registrar pedido
const nuevoPedido = await pedidosService.registrar({
  cliente: 'Juan Pérez',
  descripcion: 'Compra de equipos',
  cantidadSolicitada: 5,
  estado: 'PENDIENTE'
})

// Cambiar estado
await pedidosService.cambiarEstado(1, 'PROCESANDO')
```

### Transportistas (ms-transportistas)

```javascript
// Listar transportistas
const transportistas = await transportistasService.listar()

// Listar disponibles
const disponibles = await transportistasService.listarDisponibles()

// Registrar transportista
const nuevoTransportista = await transportistasService.registrar({
  nombre: 'Juan Pérez',
  patente: 'ABC-1234',
  telefono: '1234567890',
  disponible: true
})

// Cambiar disponibilidad
await transportistasService.cambiarDisponibilidad('id-uuid', false)
```

### Envíos (ms-envios)

```javascript
// Listar envíos
const envios = await enviosService.listar()

// Listar pendientes
const pendientes = await enviosService.listarDisponibles()

// Registrar envío
const nuevoEnvio = await enviosService.registrar({
  pedido_id: 1,
  transportista_id: 'uuid-transportista',
  direccion_entrega: 'Calle Principal 123',
  estado: 'PENDIENTE'
})

// Cambiar estado
await enviosService.cambiarEstado('id-envio', 'EN_RUTA')
```

## 🛠️ Scripts Disponibles

```bash
# Desarrollo
npm run dev          # Inicia servidor en http://localhost:5173

# Compilación
npm run build        # Build para producción (dist/)

# Linting
npm run lint         # Verifica estilo de código (ESLint)

# Preview
npm run preview      # Vista previa de build (dist/)
```

## 📊 Stack Tecnológico

| Herramienta | Versión | Propósito |
|-----------|---------|----------|
| **React** | 18+ | Framework UI |
| **Vite** | 5+ | Build tool y dev server |
| **JavaScript ES6** | - | Lenguaje |
| **CSS3** | - | Estilos |
| **ESLint** | - | Linting |

## 🌐 Comunicación con Microservicios

### Flujo de Peticiones

```
Frontend (5173)
    ↓
apiService.js (centraliza credenciales)
    ↓
POST /api/login (validación)
    ↓
Microservicio (8081, 8082, 8083, 8084)
    ↓
MySQL (smartlogix)
```

### Headers Automáticos

```javascript
// Cada petición incluye automáticamente:
{
  'Authorization': 'Basic dXNlcjpwYXNzd29yZA==',
  'Content-Type': 'application/json'
}
```

## 🔒 Seguridad (Desarrollo)

**Configuración actual (desarrollo)**:
- ✅ CORS habilitado para localhost:5173
- ✅ Autenticación Basic Auth
- ✅ CSRF deshabilitado (para APIs stateless)

**Para Producción** debes implementar:
- 🔐 Autenticación JWT o OAuth2
- 🔐 Autorización basada en roles (RBAC)
- 🔐 Validación de CORS más restrictiva
- 🔐 HTTPS/TLS
- 🔐 Rate limiting
- 🔐 Validación de input rigurosa

## 🐛 Troubleshooting

### Puerto 5173 ya está en uso
```bash
# Vite usará automáticamente el siguiente puerto disponible
# O especifica uno manualmente:
npm run dev -- --port 3000
```

### "Connection refused" (Microservicios no disponibles)
```bash
# Verifica que todos los microservicios estén corriendo:
# Terminal 1: cd ms-logistics-base && mvn spring-boot:run
# Terminal 2: cd ms-pedidos && mvn spring-boot:run
# Terminal 3: cd ms-transportistas && mvn spring-boot:run
# Terminal 4: cd ms-envios && mvn spring-boot:run
```

### "401 Unauthorized"
```bash
# Verifica las credenciales en .env.local:
VITE_API_USERNAME=user        # Debe coincidir con Spring Security
VITE_API_PASSWORD=password    # Debe coincidir con Spring Security
```

### CORS errors
```bash
# Verifica que la URL del frontend (5173) esté en CORS de los microservicios
# Edita application.properties en cada microservicio:
# cors.allowed-origins=http://localhost:5173
```

### Módulos de Vite no encontrados
```bash
# Reinstala las dependencias:
rm -rf node_modules package-lock.json
npm install
npm run dev
```

## 📝 Notas Importantes

- Las credenciales se almacenan en `.env.local` (NO commitir)
- Cada cambio en `.env.local` requiere reiniciar el servidor
- La cobertura de código se ve en [GUIA_EJECUCION_JACOCO.md](../GUIA_EJECUCION_JACOCO.md)
- Para más detalles de autenticación: [AUTENTICACION_BASIC_AUTH.md](AUTENTICACION_BASIC_AUTH.md)

## 📖 Documentación Adicional

- [AUTENTICACION_BASIC_AUTH.md](AUTENTICACION_BASIC_AUTH.md) - Detalles de autenticación
- [EJEMPLOS_EXTENSION_API.md](EJEMPLOS_EXTENSION_API.md) - Cómo extender el servicio API
- [../README.md](../README.md) - Documentación general del proyecto
- [../GUIA_EJECUCION_JACOCO.md](../GUIA_EJECUCION_JACOCO.md) - Cobertura de código

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

