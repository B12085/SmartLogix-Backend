/**
 * EJEMPLOS DE EXTENSIÓN DEL SERVICIO API
 * Este archivo muestra cómo agregar nuevos endpoints y microservicios
 */

// ============================================================
// EJEMPLO 1: Agregar más métodos al servicio existente
// ============================================================

// En src/services/apiService.js, agregar dentro de inventarioService:

export const inventarioService = {
  // ... métodos existentes ...

  /**
   * Buscar productos por nombre
   */
  buscarProductosPorNombre: async (nombre) => {
    const response = await fetchConAuth(
      `${API_INVENTARIO_URL}/api/productos/buscar?nombre=${encodeURIComponent(nombre)}`
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudieron buscar los productos`)
  },

  /**
   * Obtener un producto específico por SKU
   */
  obtenerProducto: async (sku) => {
    const response = await fetchConAuth(
      `${API_INVENTARIO_URL}/api/productos/${sku}`
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se encontró el producto`)
  },

  /**
   * Aplicar descuento a un producto
   */
  aplicarDescuento: async (sku, descuentoPorcentaje) => {
    const response = await fetchConAuth(
      `${API_INVENTARIO_URL}/api/productos/${sku}/descuento`,
      {
        method: 'PATCH',
        body: JSON.stringify({ descuentoPorcentaje })
      }
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo aplicar el descuento`)
  }
}

// ============================================================
// EJEMPLO 2: Agregar un nuevo microservicio
// ============================================================

// Si tienes un microservicio de USUARIOS en http://localhost:8083:

const API_USUARIOS_URL = import.meta.env.VITE_API_USUARIOS_URL || 'http://localhost:8083'

export const usuariosService = {
  /**
   * Obtener todos los usuarios
   */
  listarUsuarios: async () => {
    const response = await fetchConAuth(`${API_USUARIOS_URL}/api/usuarios/listar`)
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudieron cargar los usuarios`)
  },

  /**
   * Registrar nuevo usuario
   */
  registrarUsuario: async (usuario) => {
    const response = await fetchConAuth(
      `${API_USUARIOS_URL}/api/usuarios/registrar`,
      {
        method: 'POST',
        body: JSON.stringify(usuario)
      }
    )
    if (response.status === 201 || response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo registrar el usuario`)
  },

  /**
   * Cambiar rol de un usuario
   */
  cambiarRol: async (usuarioId, nuevoRol) => {
    const response = await fetchConAuth(
      `${API_USUARIOS_URL}/api/usuarios/${usuarioId}/rol`,
      {
        method: 'PUT',
        body: JSON.stringify({ rol: nuevoRol })
      }
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo cambiar el rol`)
  }
}

// ============================================================
// EJEMPLO 3: Agregar manejo de errores más avanzado
// ============================================================

// Versión mejorada de fetchConAuth con reintentos automáticos:

const MAX_REINTENTOS = 3

const fetchConAuthYReintentos = async (url, options = {}, intento = 0) => {
  try {
    const response = await fetchConAuth(url, options)
    return response
  } catch (error) {
    // Reintentar solo para errores de red, no para 401
    if (intento < MAX_REINTENTOS && error.message !== 'Credenciales inválidas') {
      console.log(`🔄 Reintentando (${intento + 1}/${MAX_REINTENTOS})...`)
      await new Promise(resolve => setTimeout(resolve, 1000)) // Esperar 1 segundo
      return fetchConAuthYReintentos(url, options, intento + 1)
    }
    throw error
  }
}

// ============================================================
// EJEMPLO 4: Agregar interceptor para todas las peticiones
// ============================================================

// Si necesitas loguear o trackear todas las peticiones:

const fetchConAuthYLog = async (url, options = {}) => {
  const inicio = Date.now()
  
  console.log(`📤 REQUEST: ${options.method || 'GET'} ${url}`)

  try {
    const response = await fetchConAuth(url, options)
    const duracion = Date.now() - inicio
    console.log(`✅ RESPONSE: ${response.status} (${duracion}ms)`)
    return response
  } catch (error) {
    const duracion = Date.now() - inicio
    console.error(`❌ ERROR: ${error.message} (${duracion}ms)`)
    throw error
  }
}

// ============================================================
// EJEMPLO 5: Usar en React Component
// ============================================================

// En App.jsx o cualquier componente:

/*
import { inventarioService, pedidosService, usuariosService } from './services/apiService'

function MiComponente() {
  const [productos, setProductos] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)

  // Cargar productos
  const cargarDatos = async () => {
    setCargando(true)
    setError(null)
    try {
      const [datosProductos, datosUsuarios] = await Promise.all([
        inventarioService.listarProductos(),
        usuariosService.listarUsuarios()
      ])
      setProductos(datosProductos)
      setUsuarios(datosUsuarios)
    } catch (err) {
      setError(err.message)
      console.error('Error cargando datos:', err)
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarDatos()
  }, [])

  if (cargando) return <div>Cargando...</div>
  if (error) return <div>Error: {error}</div>

  return (
    <div>
      <h2>Productos: {productos.length}</h2>
      <h2>Usuarios: {usuarios.length}</h2>
    </div>
  )
}
*/

// ============================================================
// EJEMPLO 6: Configurar diferentes credenciales por servicio
// ============================================================

// En .env:
/*
VITE_API_INVENTARIO_URL=http://localhost:8081
VITE_API_INVENTARIO_USERNAME=user_inventario
VITE_API_INVENTARIO_PASSWORD=pass_inventario

VITE_API_PEDIDOS_URL=http://localhost:8082
VITE_API_PEDIDOS_USERNAME=user_pedidos
VITE_API_PEDIDOS_PASSWORD=pass_pedidos
*/

// En apiService.js:
/*
const getBasicAuthHeaderPara = (username, password) => {
  const credentials = `${username}:${password}`
  const encodedCredentials = btoa(credentials)
  return `Basic ${encodedCredentials}`
}

const fetchConAuthCustom = async (url, username, password, options = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': getBasicAuthHeaderPara(username, password),
    ...options.headers
  }

  const response = await fetch(url, {
    ...options,
    headers
  })

  if (response.status === 401) {
    throw new Error('Credenciales inválidas para este servicio')
  }

  return response
}

export const inventarioService = {
  listarProductos: async () => {
    const response = await fetchConAuthCustom(
      `${API_INVENTARIO_URL}/api/productos/listar`,
      import.meta.env.VITE_API_INVENTARIO_USERNAME,
      import.meta.env.VITE_API_INVENTARIO_PASSWORD
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}`)
  }
  // ... más métodos
}
*/

// ============================================================
// EJEMPLO 7: Tipos TypeScript (si usas TypeScript)
// ============================================================

/*
// types/api.ts

export interface Producto {
  sku: string
  nombre: string
  descripcion?: string
  cantidadStock: number
  precio: number
}

export interface Pedido {
  id?: number
  numeroPedido: string
  cliente: string
  descripcion?: string
  skuProducto: string
  cantidadSolicitada: number
}

export interface Usuario {
  id?: number
  nombre: string
  email: string
  rol: 'ADMIN' | 'USER' | 'VENDEDOR'
}

// apiService.ts

export const inventarioService = {
  listarProductos: async (): Promise<Producto[]> => { ... },
  registrarProducto: async (producto: Producto): Promise<Producto> => { ... },
  actualizarProducto: async (sku: string, producto: Partial<Producto>): Promise<Producto> => { ... },
  eliminarProducto: async (sku: string): Promise<boolean> => { ... }
}
*/

export default {
  // Exportar los servicios
}
