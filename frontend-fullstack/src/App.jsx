import { useState, useEffect } from 'react'
import { inventarioService, pedidosService } from './services/apiService'

function App() {
  const [pestanaActiva, setPestanaActiva] = useState('inventario')
  const [productos, setProductos] = useState([])
  const [pedidos, setPedidos] = useState([])
  const [cargando, setCargando] = useState(false)

  // Estado del formulario de Inventario
  const [formInventario, setFormInventario] = useState({
    sku: '',
    nombre: '',
    descripcion: '',
    cantidadStock: '',
    precio: ''
  })
  const [editandoProducto, setEditandoProducto] = useState(null)

  // Estado del formulario de Pedidos
  const [formPedido, setFormPedido] = useState({
    numeroPedido: '',
    cliente: '',
    descripcion: '',
    productoId: '',
    cantidadSolicitada: ''
  })

  // Cargar productos al montar el componente
  useEffect(() => {
    cargarProductos()
    cargarPedidos()
  }, [])

  // ==================== FUNCIONES DE INVENTARIO ====================
  const cargarProductos = async () => {
    setCargando(true)
    try {
      const datos = await inventarioService.listarProductos()
      setProductos(datos)
    } catch (error) {
      console.error('Error:', error.message)
      alert(`Error al cargar los productos: ${error.message}`)
    }
    setCargando(false)
  }

  const manejarCambioInventario = (e) => {
    const { name, value } = e.target
    setFormInventario({ ...formInventario, [name]: value })
  }

  const guardarProducto = async (e) => {
    e.preventDefault()

    if (!formInventario.sku || !formInventario.nombre || !formInventario.precio) {
      alert('Por favor completa los campos requeridos: SKU, Nombre y Precio')
      return
    }

    setCargando(true)
    try {
      const datosProducto = {
        sku: formInventario.sku,
        nombre: formInventario.nombre,
        descripcion: formInventario.descripcion,
        cantidadStock: parseInt(formInventario.cantidadStock) || 0,
        precio: parseFloat(formInventario.precio)
      }

      if (editandoProducto) {
        // Actualizar producto existente
        await inventarioService.actualizarProducto(editandoProducto.sku, datosProducto)
        alert('Producto actualizado correctamente')
      } else {
        // Registrar nuevo producto
        await inventarioService.registrarProducto(datosProducto)
        alert('Producto registrado correctamente')
      }

      setFormInventario({ sku: '', nombre: '', descripcion: '', cantidadStock: '', precio: '' })
      setEditandoProducto(null)
      await cargarProductos()
    } catch (error) {
      console.error('Error:', error.message)
      alert(`Error: ${error.message}`)
    }
    setCargando(false)
  }

  const editarProducto = (producto) => {
    setFormInventario({
      sku: producto.sku,
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      cantidadStock: producto.cantidadStock,
      precio: producto.precio
    })
    setEditandoProducto(producto)
  }

  const eliminarProducto = async (sku) => {
    if (!confirm(`¿Está seguro de eliminar el producto ${sku}?`)) return

    setCargando(true)
    try {
      await inventarioService.eliminarProducto(sku)
      alert('Producto eliminado correctamente')
      await cargarProductos()
    } catch (error) {
      console.error('Error:', error.message)
      alert(`Error: ${error.message}`)
    }
    setCargando(false)
  }

  const cancelarEdicion = () => {
    setFormInventario({ sku: '', nombre: '', descripcion: '', cantidadStock: '', precio: '' })
    setEditandoProducto(null)
  }

  // ==================== FUNCIONES DE PEDIDOS ====================
  const normalizarPedido = (pedido) => {
    return {
      ...pedido,
      numeroPedido: pedido.numeroPedido ?? pedido.numero_pedido,
      skuProducto: pedido.skuProducto ?? pedido.sku_producto,
      cantidadSolicitada: pedido.cantidadSolicitada ?? pedido.cantidad_solicitada,
      fecha: pedido.fecha ?? pedido.fecha_creacion
    }
  }

  const cargarPedidos = async () => {
    setCargando(true)
    try {
      const datos = await pedidosService.listarPedidos()
      setPedidos(datos.map(normalizarPedido))
    } catch (error) {
      console.error('Error:', error.message)
      alert(`Error al cargar los pedidos: ${error.message}`)
    }
    setCargando(false)
  }

  const manejarCambioPedido = (e) => {
    const { name, value } = e.target
    setFormPedido({ ...formPedido, [name]: value })
  }

  const registrarPedido = async (e) => {
    e.preventDefault()

    if (!formPedido.numeroPedido || !formPedido.cliente || !formPedido.productoId || !formPedido.cantidadSolicitada) {
      alert('Por favor completa todos los campos del formulario')
      return
    }

    setCargando(true)
    try {
      const productoSeleccionado = productos.find((p) => p.id === formPedido.productoId)
      await pedidosService.registrarPedido({
        numero_pedido: formPedido.numeroPedido,
        cliente: formPedido.cliente,
        descripcion: formPedido.descripcion,
        producto_id: formPedido.productoId,
        sku_producto: productoSeleccionado?.sku || '',
        cantidad_solicitada: parseInt(formPedido.cantidadSolicitada)
      })
      alert('Pedido registrado correctamente')

      setFormPedido({ numeroPedido: '', cliente: '', descripcion: '', productoId: '', cantidadSolicitada: '' })
      await cargarPedidos()
      await cargarProductos()
    } catch (error) {
      console.error('Error:', error.message)
      alert(`Error: ${error.message}`)
    }
    setCargando(false)
  }

  // ==================== ESTILOS EN LÍNEA ====================
  const estilos = {
    contenedor: {
      fontFamily: 'Segoe UI, Tahoma, Geneva, Verdana, sans-serif',
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '20px',
      backgroundColor: 'var(--app-bg)',
      minHeight: '100vh'
    },
    encabezado: {
      textAlign: 'center',
      marginBottom: '30px',
      color: 'var(--text-h)'
    },
    titulo: {
      fontSize: '2.5em',
      margin: '10px 0',
      color: 'var(--text-h)'
    },
    subtitulo: {
      color: 'var(--text)',
      fontSize: '1.1em'
    },
    navegacionPestanas: {
      display: 'flex',
      gap: '10px',
      marginBottom: '30px',
      borderBottom: '1px solid var(--divider)',
    },
    botonPestana: {
      padding: '12px 24px',
      border: 'none',
      backgroundColor: 'transparent',
      cursor: 'pointer',
      fontSize: '1em',
      fontWeight: '500',
      color: 'var(--text)',
      borderBottom: '3px solid transparent',
      transition: 'all 0.3s ease'
    },
    botonPestanaActiva: {
      color: 'var(--text-h)',
      borderBottom: '3px solid var(--brand-primary)'
    },
    seccion: {
      backgroundColor: 'var(--surface)',
      padding: '30px',
      borderRadius: '8px',
      boxShadow: 'var(--shadow)'
    },
    formulario: {
      marginBottom: '30px',
      padding: '20px',
      backgroundColor: 'var(--surface-muted)',
      borderRadius: '6px',
      borderLeft: '4px solid var(--brand-primary)'
    },
    grupo: {
      marginBottom: '15px'
    },
    etiqueta: {
      display: 'block',
      marginBottom: '6px',
      fontWeight: '600',
      color: 'var(--text-h)'
    },
    entrada: {
      width: '100%',
      padding: '10px',
      border: '1px solid var(--divider)',
      borderRadius: '4px',
      fontSize: '1em',
      boxSizing: 'border-box',
      fontFamily: 'inherit'
    },
    grupoInputs: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
      gap: '15px',
      marginBottom: '15px'
    },
    botones: {
      display: 'flex',
      gap: '10px',
      marginTop: '20px'
    },
    boton: {
      padding: '10px 20px',
      border: 'none',
      borderRadius: '4px',
      cursor: 'pointer',
      fontSize: '1em',
      fontWeight: '600',
      transition: 'all 0.2s ease'
    },
    botonPrimario: {
      backgroundColor: 'var(--brand-primary)',
      color: 'white'
    },
    botonPrimarioHover: {
      backgroundColor: 'var(--brand-primary-hover)'
    },
    botonSecundario: {
      backgroundColor: 'var(--text)',
      color: 'white'
    },
    botonSecundarioHover: {
      backgroundColor: 'var(--text-h)'
    },
    botonDanger: {
      backgroundColor: 'var(--brand-danger)',
      color: 'white'
    },
    botonDangerHover: {
      backgroundColor: 'var(--brand-danger)'
    },
    tabla: {
      width: '100%',
      borderCollapse: 'collapse',
      marginTop: '20px'
    },
    th: {
      backgroundColor: 'var(--surface-muted)',
      color: 'var(--text-h)',
      padding: '12px',
      textAlign: 'left',
      fontWeight: '600',
      borderBottom: '1px solid var(--divider)'
    },
    td: {
      padding: '12px',
      borderBottom: '1px solid var(--divider)'
    },
    filaPar: {
      backgroundColor: 'var(--surface-muted)'
    },
    filaImpar: {
      backgroundColor: 'var(--surface)'
    },
    accionesTabla: {
      display: 'flex',
      gap: '8px'
    },
    botonTabla: {
      padding: '6px 12px',
      fontSize: '0.9em',
      border: 'none',
      borderRadius: '3px',
      cursor: 'pointer',
      transition: 'all 0.2s ease'
    },
    botonEditar: {
      backgroundColor: 'var(--brand-warning)',
      color: 'white'
    },
    botonEliminar: {
      backgroundColor: 'var(--brand-danger)',
      color: 'white'
    },
    mensajeCargando: {
      textAlign: 'center',
      padding: '20px',
      color: 'var(--text)',
      fontSize: '1.1em'
    }
  }

  // ==================== RENDER ====================
  return (
    <div style={estilos.contenedor}>
      <div style={estilos.encabezado}>
        <h1 style={estilos.titulo}>SmartLogix</h1>
        <p style={estilos.subtitulo}>Sistema de Gestión de Inventario y Pedidos</p>
      </div>

      {/* NAVEGACIÓN DE PESTAÑAS */}
      <div style={estilos.navegacionPestanas}>
        <button
          style={{
            ...estilos.botonPestana,
            ...(pestanaActiva === 'inventario' ? estilos.botonPestanaActiva : {})
          }}
          onClick={() => setPestanaActiva('inventario')}
        >
          Gestión de Inventario
        </button>
        <button
          style={{
            ...estilos.botonPestana,
            ...(pestanaActiva === 'pedidos' ? estilos.botonPestanaActiva : {})
          }}
          onClick={() => setPestanaActiva('pedidos')}
        >
          Gestión de Pedidos
        </button>
      </div>

      {/* SECCIÓN DE INVENTARIO */}
      {pestanaActiva === 'inventario' && (
        <div style={estilos.seccion}>
          <h2 style={{ color: 'var(--text-h)', marginBottom: '20px' }}>
            {editandoProducto ? 'Editar Producto' : 'Registrar Nuevo Producto'}
          </h2>

          <form onSubmit={guardarProducto} style={estilos.formulario}>
            <div style={estilos.grupoInputs}>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>SKU *</label>
                <input
                  type="text"
                  name="sku"
                  value={formInventario.sku}
                  onChange={manejarCambioInventario}
                  placeholder="Ej: PROD-001"
                  style={estilos.entrada}
                  disabled={editandoProducto}
                />
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Nombre *</label>
                <input
                  type="text"
                  name="nombre"
                  value={formInventario.nombre}
                  onChange={manejarCambioInventario}
                  placeholder="Ej: Laptop Dell"
                  style={estilos.entrada}
                />
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Cantidad Stock</label>
                <input
                  type="number"
                  name="cantidadStock"
                  value={formInventario.cantidadStock}
                  onChange={manejarCambioInventario}
                  placeholder="0"
                  style={estilos.entrada}
                  min="0"
                />
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Precio *</label>
                <input
                  type="number"
                  name="precio"
                  value={formInventario.precio}
                  onChange={manejarCambioInventario}
                  placeholder="0.00"
                  style={estilos.entrada}
                  step="0.01"
                  min="0"
                />
              </div>
            </div>

            <div style={estilos.grupo}>
              <label style={estilos.etiqueta}>Descripción</label>
              <input
                type="text"
                name="descripcion"
                value={formInventario.descripcion}
                onChange={manejarCambioInventario}
                placeholder="Descripción del producto"
                style={{...estilos.entrada, minHeight: '60px'}}
              />
            </div>

            <div style={estilos.botones}>
              <button
                type="submit"
                style={{
                  ...estilos.boton,
                  ...estilos.botonPrimario
                }}
                onMouseOver={(e) => e.target.style.backgroundColor = 'var(--brand-primary-hover)'}
                onMouseOut={(e) => e.target.style.backgroundColor = 'var(--brand-primary)'}
                disabled={cargando}
              >
                {cargando ? 'Guardando...' : (editandoProducto ? 'Actualizar' : 'Registrar')}
              </button>
              {editandoProducto && (
                <button
                  type="button"
                  style={{
                    ...estilos.boton,
                    ...estilos.botonSecundario
                  }}
                  onClick={cancelarEdicion}
                  disabled={cargando}
                >
                  Cancelar
                </button>
              )}
            </div>
          </form>

          <hr style={{ margin: '30px 0', border: 'none', borderTop: '1px solid var(--divider)' }} />

          <h2 style={{ color: 'var(--text-h)', marginBottom: '20px' }}>Listado de Productos</h2>

          {cargando ? (
            <div style={estilos.mensajeCargando}>Cargando productos...</div>
          ) : productos.length === 0 ? (
            <div style={estilos.mensajeCargando}>No hay productos registrados</div>
          ) : (
            <table style={estilos.tabla}>
              <thead>
                <tr>
                  <th style={estilos.th}>SKU</th>
                  <th style={estilos.th}>Nombre</th>
                  <th style={estilos.th}>Descripción</th>
                  <th style={estilos.th}>Stock</th>
                  <th style={estilos.th}>Precio</th>
                  <th style={estilos.th}>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {productos.map((producto, índice) => (
                  <tr
                    key={producto.sku}
                    style={índice % 2 === 0 ? estilos.filaPar : estilos.filaImpar}
                  >
                    <td style={estilos.td}><strong>{producto.sku}</strong></td>
                    <td style={estilos.td}>{producto.nombre}</td>
                    <td style={estilos.td}>{producto.descripcion || '-'}</td>
                    <td style={{...estilos.td, fontWeight: producto.cantidadStock < 10 ? 'bold' : 'normal', color: producto.cantidadStock < 10 ? 'var(--brand-danger)' : 'var(--brand-success)'}}>
                      {producto.cantidadStock}
                    </td>
                    <td style={estilos.td}>${producto.precio.toFixed(2)}</td>
                    <td style={estilos.td}>
                      <div style={estilos.accionesTabla}>
                        <button
                          style={{...estilos.botonTabla, ...estilos.botonEditar}}
                          onClick={() => editarProducto(producto)}
                          disabled={cargando}
                        >
                          Editar
                        </button>
                        <button
                          style={{...estilos.botonTabla, ...estilos.botonEliminar}}
                          onClick={() => eliminarProducto(producto.sku)}
                          disabled={cargando}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* SECCIÓN DE PEDIDOS */}
      {pestanaActiva === 'pedidos' && (
        <div style={estilos.seccion}>
          <h2 style={{ color: 'var(--text-h)', marginBottom: '20px' }}>Registrar Nuevo Pedido</h2>

          <form onSubmit={registrarPedido} style={estilos.formulario}>
            <div style={estilos.grupoInputs}>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Número de Pedido *</label>
                <input
                  type="text"
                  name="numeroPedido"
                  value={formPedido.numeroPedido}
                  onChange={manejarCambioPedido}
                  placeholder="Ej: PED-001"
                  style={estilos.entrada}
                />
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Cliente *</label>
                <input
                  type="text"
                  name="cliente"
                  value={formPedido.cliente}
                  onChange={manejarCambioPedido}
                  placeholder="Nombre del cliente"
                  style={estilos.entrada}
                />
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Producto *</label>
                <select
                  name="productoId"
                  value={formPedido.productoId}
                  onChange={manejarCambioPedido}
                  style={estilos.entrada}
                >
                  <option value="" disabled>Selecciona un producto</option>
                  {productos.map((producto) => (
                    <option key={producto.id} value={producto.id}>
                      {producto.nombre} ({producto.sku}) - Stock: {producto.cantidadStock}
                    </option>
                  ))}
                </select>
              </div>
              <div style={estilos.grupo}>
                <label style={estilos.etiqueta}>Cantidad Solicitada *</label>
                <input
                  type="number"
                  name="cantidadSolicitada"
                  value={formPedido.cantidadSolicitada}
                  onChange={manejarCambioPedido}
                  placeholder="0"
                  style={estilos.entrada}
                  min="1"
                />
              </div>
            </div>

            <div style={estilos.grupo}>
              <label style={estilos.etiqueta}>Descripción</label>
              <input
                type="text"
                name="descripcion"
                value={formPedido.descripcion}
                onChange={manejarCambioPedido}
                placeholder="Detalles adicionales del pedido"
                style={{...estilos.entrada, minHeight: '60px'}}
              />
            </div>

            <div style={estilos.botones}>
              <button
                type="submit"
                style={{
                  ...estilos.boton,
                  ...estilos.botonPrimario
                }}
                onMouseOver={(e) => e.target.style.backgroundColor = 'var(--brand-primary-hover)'}
                onMouseOut={(e) => e.target.style.backgroundColor = 'var(--brand-primary)'}
                disabled={cargando}
              >
                {cargando ? 'Registrando...' : 'Registrar Pedido'}
              </button>
            </div>
          </form>

          <hr style={{ margin: '30px 0', border: 'none', borderTop: '1px solid var(--divider)' }} />

          <h2 style={{ color: 'var(--text-h)', marginBottom: '20px' }}>Listado de Pedidos</h2>

          {cargando ? (
            <div style={estilos.mensajeCargando}>Cargando pedidos...</div>
          ) : pedidos.length === 0 ? (
            <div style={estilos.mensajeCargando}>No hay pedidos registrados</div>
          ) : (
            <table style={estilos.tabla}>
              <thead>
                <tr>
                  <th style={estilos.th}>Número de Pedido</th>
                  <th style={estilos.th}>Cliente</th>
                  <th style={estilos.th}>SKU Producto</th>
                  <th style={estilos.th}>Cantidad</th>
                  <th style={estilos.th}>Descripción</th>
                  <th style={estilos.th}>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map((pedido, índice) => (
                  <tr
                    key={pedido.numeroPedido}
                    style={índice % 2 === 0 ? estilos.filaPar : estilos.filaImpar}
                  >
                    <td style={estilos.td}><strong>{pedido.numeroPedido}</strong></td>
                    <td style={estilos.td}>{pedido.cliente}</td>
                    <td style={estilos.td}>{pedido.skuProducto}</td>
                    <td style={estilos.td}>{pedido.cantidadSolicitada}</td>
                    <td style={estilos.td}>{pedido.descripcion || '-'}</td>
                    <td style={estilos.td}>{new Date(pedido.fecha).toLocaleDateString('es-ES') || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  )
}

export default App
