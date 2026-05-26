/**
 * Servicio centralizado para hacer peticiones HTTP a los microservicios
 * CORS habilitado en los backends - Sin autenticación requerida
 */

const API_INVENTARIO_URL = import.meta.env.VITE_API_INVENTARIO_URL
const API_PEDIDOS_URL = import.meta.env.VITE_API_PEDIDOS_URL

/**
 * Realiza una petición HTTP con headers CORS
 * @param {string} url - URL completa del endpoint
 * @param {object} options - Opciones de fetch (method, body, etc.)
 * @returns {Promise<Response>} Respuesta del servidor
 */
const fetchRequest = async (url, options = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers
    })

    if (!response.ok && response.status !== 204) {
      throw new Error(`HTTP ${response.status}`)
    }

    return response
  } catch (error) {
    console.error('Error en la petición:', error.message)
    throw error
  }
}

// ==================== SERVICIOS DE INVENTARIO ====================

export const inventarioService = {
  /**
   * Obtiene la lista de todos los productos
   */
  listarProductos: async () => {
    const response = await fetchRequest(`${API_INVENTARIO_URL}/api/productos/listar`)
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudieron cargar los productos`)
  },

  /**
   * Registra un nuevo producto
   */
  registrarProducto: async (producto) => {
    const response = await fetchRequest(
      `${API_INVENTARIO_URL}/api/productos/registrar`,
      {
        method: 'POST',
        body: JSON.stringify(producto)
      }
    )
    if (response.status === 201 || response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo registrar el producto`)
  },

  /**
   * Actualiza un producto existente
   */
  actualizarProducto: async (sku, producto) => {
    const response = await fetchRequest(
      `${API_INVENTARIO_URL}/api/productos/actualizar/${sku}`,
      {
        method: 'PUT',
        body: JSON.stringify(producto)
      }
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo actualizar el producto`)
  },

  /**
   * Elimina un producto por SKU
   */
  eliminarProducto: async (sku) => {
    const response = await fetchRequest(
      `${API_INVENTARIO_URL}/api/productos/eliminar/${sku}`,
      {
        method: 'DELETE'
      }
    )
    if (response.ok || response.status === 204) {
      return true
    }
    throw new Error(`Error ${response.status}: No se pudo eliminar el producto`)
  },

  /**
   * Actualiza el stock de un producto
   * @param {string} sku - SKU del producto
   * @param {number} cantidad - Cantidad a restar (debe ser positiva)
   */
  actualizarStock: async (sku, cantidad) => {
    const skuEncoded = encodeURIComponent(sku)
    const response = await fetchRequest(
      `${API_INVENTARIO_URL}/api/productos/actualizar-stock?sku=${skuEncoded}&cantidad=${cantidad}`,
      {
        method: 'PUT'
      }
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo actualizar el stock`)
  }
}

// ==================== SERVICIOS DE PEDIDOS ====================

export const pedidosService = {
  /**
   * Obtiene la lista de todos los pedidos
   */
  listarPedidos: async () => {
    const response = await fetchRequest(`${API_PEDIDOS_URL}/api/pedidos/listar`)
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudieron cargar los pedidos`)
  },

  /**
   * Registra un nuevo pedido
   */
  registrarPedido: async (pedido) => {
    const response = await fetchRequest(
      `${API_PEDIDOS_URL}/api/pedidos/registrar`,
      {
        method: 'POST',
        body: JSON.stringify(pedido)
      }
    )
    if (response.status === 201 || response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo registrar el pedido`)
  },

  /**
   * Actualiza un pedido existente
   */
  actualizarPedido: async (id, pedido) => {
    const response = await fetchRequest(
      `${API_PEDIDOS_URL}/api/pedidos/actualizar/${id}`,
      {
        method: 'PUT',
        body: JSON.stringify(pedido)
      }
    )
    if (response.ok) {
      return await response.json()
    }
    throw new Error(`Error ${response.status}: No se pudo actualizar el pedido`)
  },

  /**
   * Elimina un pedido
   */
  eliminarPedido: async (id) => {
    const response = await fetchRequest(
      `${API_PEDIDOS_URL}/api/pedidos/eliminar/${id}`,
      {
        method: 'DELETE'
      }
    )
    if (response.ok || response.status === 204) {
      return true
    }
    throw new Error(`Error ${response.status}: No se pudo eliminar el pedido`)
  }
}

// ==================== SERVICIO GENÉRICO ====================

export const apiService = {
  /**
   * Realiza una petición HTTP genérica
   * Útil para endpoints custom o nuevos microservicios
   */
  request: async (url, options = {}) => {
    return await fetchRequest(url, options)
  }
}

export default {
  inventarioService,
  pedidosService,
  apiService
}
