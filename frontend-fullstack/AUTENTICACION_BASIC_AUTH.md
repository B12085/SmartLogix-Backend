# 🔐 Guía de Autenticación Basic Auth en el Proyecto

## 📌 Descripción General

Este proyecto está configurado para conectarse a dos microservicios Spring Boot con **Spring Security** activado y **Basic Auth** como mecanismo de autenticación.

### URLs de los Microservicios
- **Inventario**: `http://localhost:8081`
- **Pedidos**: `http://localhost:8082`
- **Credenciales**: `user` / `password`

---

## 🔧 Configuración Inicial

### 1. Configurar variables de entorno (`.env`)

```bash
# URLs de los microservicios
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_PEDIDOS_URL=http://localhost:8082

# Credenciales Basic Auth
VITE_API_USERNAME=user
VITE_API_PASSWORD=password
```

> ⚠️ **IMPORTANTE**: El archivo `.env` NO debe commitirse a Git. Ya está en `.gitignore`.

---

## 🚀 Cómo Funciona Basic Auth en Este Proyecto

### ¿Qué es Basic Auth?

Basic Auth es un método simple de autenticación HTTP donde:
1. Se concatenan las credenciales: `usuario:contraseña`
2. Se codifican en Base64
3. Se envían en el header `Authorization: Basic <base64>`

**Ejemplo:**
```
usuario: "user"
contraseña: "password"
Resultado: "Basic dXNlcjpwYXNzd29yZA=="
```

### Código Base64 en JavaScript

```javascript
const credentials = "user:password"
const encodedCredentials = btoa(credentials)
console.log(encodedCredentials) // "dXNlcjpwYXNzd29yZA=="
```

---

## 💻 Ejemplos de Código

### ✅ Opción 1: Usando Fetch API (RECOMENDADO - Sin dependencias)

```javascript
// En apiService.js (ya implementado en este proyecto)

const getBasicAuthHeader = () => {
  const credentials = `${API_USERNAME}:${API_PASSWORD}`
  const encodedCredentials = btoa(credentials) // Base64
  return `Basic ${encodedCredentials}`
}

const fetchConAuth = async (url, options = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': getBasicAuthHeader(),
    ...options.headers
  }

  const response = await fetch(url, {
    ...options,
    headers
  })

  if (response.status === 401) {
    throw new Error('Credenciales inválidas. Verifica tu .env')
  }

  return response
}

// Uso:
const datos = await fetchConAuth('http://localhost:8081/api/productos/listar')
const json = await datos.json()
```

### ✅ Opción 2: Si usas Axios (Alternativa)

Si quieres usar Axios, primero instala:

```bash
npm install axios
```

Luego crea un servicio similar:

```javascript
// apiService_axios.js
import axios from 'axios'

const API_INVENTARIO_URL = import.meta.env.VITE_API_INVENTARIO_URL
const API_USERNAME = import.meta.env.VITE_API_USERNAME
const API_PASSWORD = import.meta.env.VITE_API_PASSWORD

// Crear instancia de Axios con Basic Auth
const axiosInstance = axios.create({
  baseURL: API_INVENTARIO_URL,
  auth: {
    username: API_USERNAME,
    password: API_PASSWORD
  }
})

// Axios automáticamente codifica en Base64 y agrega el header Authorization

export const inventarioService = {
  listarProductos: async () => {
    const { data } = await axiosInstance.get('/api/productos/listar')
    return data
  },

  registrarProducto: async (producto) => {
    const { data } = await axiosInstance.post('/api/productos/registrar', producto)
    return data
  }
}
```

---

## 🧪 Pruebas con Postman / cURL

### Usando cURL:

```bash
# GET con Basic Auth
curl -u user:password http://localhost:8081/api/productos/listar

# POST con Basic Auth
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'user:password' | base64)" \
  -d '{"sku":"PROD-001","nombre":"Laptop","precio":999.99}' \
  http://localhost:8081/api/productos/registrar
```

### Usando Postman:

1. Abre tu request
2. Ve a la pestaña **Authorization**
3. Selecciona **Basic Auth** del dropdown
4. Ingresa:
   - Username: `user`
   - Password: `password`
5. Postman automáticamente genera el header Authorization

---

## ❌ Solución de Errores Comunes

### Error 401 Unauthorized

**Causa:** Credenciales incorrectas o falta del header Authorization

**Solución:**
1. Verifica que `.env` tiene las credenciales correctas
2. Verifica que el header `Authorization` está siendo enviado
3. Comprueba en DevTools (Network tab) que el request incluye: `Authorization: Basic dXNlcjpwYXNzd29yZA==`

```javascript
// Debug: Ver qué se está enviando
const getBasicAuthHeader = () => {
  const credentials = `${API_USERNAME}:${API_PASSWORD}`
  const encodedCredentials = btoa(credentials)
  const header = `Basic ${encodedCredentials}`
  console.log('📌 Header enviado:', header)
  return header
}
```

### Error 403 Forbidden

**Causa:** Autenticado correctamente pero sin permisos para ese recurso

**Solución:**
- Verifica los permisos en Spring Security del backend
- Asegúrate de que el usuario `user` tiene acceso a ese endpoint

### Error 404 Not Found

**Causa:** URL incorrecta

**Solución:**
- Verifica que la URL en `.env` es correcta
- Comprueba que el microservicio está corriendo en ese puerto

---

## 🔄 Flujo de Autenticación en Este Proyecto

```
┌─────────────────────────────────────────────────────────┐
│ React App (Frontend)                                    │
└────────────────┬────────────────────────────────────────┘
                 │
                 ├──> import { inventarioService } from './services/apiService'
                 │
                 ├──> const datos = await inventarioService.listarProductos()
                 │
                 └──> apiService.js:
                      1. Lee VITE_API_USERNAME y VITE_API_PASSWORD
                      2. Codifica en Base64: btoa("user:password")
                      3. Crea header: "Authorization: Basic dXNlcjpwYXNzd29yZA=="
                      4. Envía fetch con el header
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│ Microservicio Spring Boot (puerto 8081 o 8082)         │
│ - Spring Security intercepta la petición                │
│ - Decodifica el header Authorization                    │
│ - Valida credenciales: user:password                    │
│ - Si es válido: Procesa la petición                     │
│ - Si es inválido: Retorna 401 Unauthorized              │
└─────────────────────────────────────────────────────────┘
```

---

## 📦 Estructura de Archivos

```
src/
├── services/
│   └── apiService.js          ← 🔐 Servicio centralizado con Basic Auth
├── App.jsx                    ← Usa inventarioService y pedidosService
├── main.jsx
└── ...

.env                           ← 🔐 Credenciales (NO commitir)
.env.example                   ← Plantilla de ejemplo
.gitignore                     ← Debe incluir .env
vite.config.js                 ← Configura lectura de .env
```

---

## ✅ Checklist de Configuración

- [ ] Archivo `.env` creado con las variables VITE_*
- [ ] Microservicio de Inventario corriendo en `http://localhost:8081`
- [ ] Microservicio de Pedidos corriendo en `http://localhost:8082`
- [ ] Credenciales correctas en `.env` (usuario: `user`)
- [ ] Servicio `apiService.js` importado en `App.jsx`
- [ ] Todas las peticiones fetch reemplazadas por `inventarioService` y `pedidosService`
- [ ] `.env` agregado a `.gitignore`
- [ ] Probado con DevTools (Network tab) - ver headers Authorization

---

## 🚨 Mejores Prácticas de Seguridad

1. **NUNCA incluyas credenciales en el código** - Usa variables de entorno
2. **NUNCA commitees `.env` a Git** - Usa `.gitignore`
3. **Usa HTTPS en producción** - Basic Auth en HTTP expone las credenciales
4. **Rota las credenciales regularmente** - Especialmente en credenciales genéricas como "user"
5. **Usa tokens JWT en producción** - Basic Auth es simple pero menos seguro que JWT

---

## 🔗 Referencias

- [MDN: HTTP authentication](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication)
- [Vite: Environment Variables](https://vitejs.dev/guide/env-and-testing.html)
- [Spring Security: Basic Authentication](https://spring.io/projects/spring-security)
- [Fetch API Documentation](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)

