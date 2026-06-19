import { useEffect, useState } from 'react'

const API_ENV = import.meta.env.VITE_API_ENVIOS_URL || 'http://localhost:8084'
const API_TRANSP = import.meta.env.VITE_API_TRANSPORTISTAS_URL || 'http://localhost:8083'

export default function Envios() {
  const [envios, setEnvios] = useState([])
  const [transportistas, setTransportistas] = useState([])
  const [cargando, setCargando] = useState(false)
  const [form, setForm] = useState({ pedidoId: '', transportistaId: '', direccionEntrega: '' })

  useEffect(() => {
    cargarEnvios()
    cargarTransportistasDisponibles()
  }, [])

  const cargarEnvios = async () => {
    setCargando(true)
    try {
      const res = await fetch(`${API_ENV}/api/envios/listar`)
      if (!res.ok) {
        const msgError = await res.text().catch(() => "Sin mensaje detallado");
        console.error(`Error del Servidor [Código ${res.status}]:`, msgError);
        setEnvios([]);
        throw new Error(`Respuesta errónea del servidor: ${res.status}`);
      }
      let data = await res.json()
      setEnvios(Array.isArray(data) ? data : [])
    } catch (err) {
      setEnvios([])
      console.error(err)
      alert('Error al cargar envíos')
    } finally {
      setCargando(false)
    }
  }

  const cargarTransportistasDisponibles = async () => {
    try {
      const res = await fetch(`${API_TRANSP}/api/transportistas/disponibles`)
      if (!res.ok) {
        setTransportistas([])
        throw new Error(`Error en el servidor: ${res.status}`)
      }
      const data = await res.json()
      setTransportistas(Array.isArray(data) ? data.filter((t) => t.disponible) : [])
    } catch (err) {
      console.error('Detalle del error capturado al cargar transportistas disponibles:', err)
      setTransportistas([])
    }
  }

  const manejarCambio = (e) => {
    const { name, value } = e.target
    setForm({ ...form, [name]: value })
  }

  const crearEnvio = async (e) => {
    e.preventDefault()
    
    // Validación defensiva de campos requeridos
    const pedidoId = form.pedidoId ? form.pedidoId.trim() : ""
    const transportistaId = form.transportistaId ? form.transportistaId.trim() : ""
    const direccionEntrega = form.direccionEntrega ? form.direccionEntrega.trim() : ""
    
    // Verificar que todos los campos estén completos
    if (!pedidoId || !transportistaId || !direccionEntrega) {
      alert('Por favor, complete todos los campos requeridos')
      return
    }
    
    // Validar que pedidoId sea un número válido
    const pedidoIdNum = Number(pedidoId)
    if (isNaN(pedidoIdNum) || pedidoIdNum <= 0) {
      alert('El ID del pedido debe ser un número válido mayor a 0')
      return
    }
    
    const payload = {
      pedidoId: pedidoIdNum,
      transportistaId: transportistaId,
      direccionEntrega: direccionEntrega,
      estado: "PENDIENTE"
    }
    
    try {
      const res = await fetch(`${API_ENV}/api/envios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      if (!res.ok) {
        const msgError = await res.text().catch(() => "Sin mensaje detallado");
        console.error(`Error del Servidor [Código ${res.status}]:`, msgError);
        throw new Error(`Respuesta errónea del servidor: ${res.status}`);
      }
      setForm({ pedidoId: '', transportistaId: '', direccionEntrega: '' })
      await cargarEnvios()
    } catch (err) {
      console.error('Error de red al crear guía de despacho:', err)
      alert('Error al crear guía de despacho')
    }
  }

  const avanzarEstado = async (id, actual) => {
    const siguiente = actual === 'PENDIENTE' ? 'EN_RUTA' : actual === 'EN_RUTA' ? 'ENTREGADO' : actual
    if (siguiente === actual) return
    try {
      const nuevoEstado = siguiente.toUpperCase()
      const res = await fetch(`${API_ENV}/api/envios/${id}/estado?estado=${encodeURIComponent(nuevoEstado)}`, {
        method: 'PUT'
      })
      if (!res.ok) {
        const errorTexto = await res.text().catch(() => 'Sin mensaje')
        console.error(`Error del servidor [Código ${res.status}]:`, errorTexto)
        setEnvios([])
        return
      }
      await cargarEnvios()
    } catch (err) {
      console.error('Error de red al actualizar el estado:', err)
      setEnvios([])
    }
  }

  // ==================== ESTILOS EN LÍNEA ====================
  const estilos = {
    seccion: {
      backgroundColor: 'var(--surface)',
      padding: '30px',
      borderRadius: '8px',
      boxShadow: 'var(--shadow)'
    },
    encabezado: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '30px'
    },
    titulo: {
      color: 'var(--text-h)',
      marginBottom: '0',
      fontSize: '1.5em',
      fontWeight: '600'
    },
    formulario: {
      marginBottom: '30px',
      padding: '20px',
      backgroundColor: 'var(--surface-muted)',
      borderRadius: '6px',
      borderLeft: '4px solid var(--brand-primary)'
    },
    grupoInputs: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
      gap: '15px',
      marginBottom: '15px'
    },
    grupo: {
      marginBottom: '0'
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
      fontFamily: 'inherit',
      backgroundColor: 'var(--surface)',
      color: 'var(--text-h)'
    },
    select: {
      width: '100%',
      padding: '10px',
      border: '1px solid var(--divider)',
      borderRadius: '4px',
      fontSize: '1em',
      boxSizing: 'border-box',
      fontFamily: 'inherit',
      backgroundColor: 'var(--surface)',
      color: 'var(--text-h)',
      cursor: 'pointer'
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
    botonSecundario: {
      backgroundColor: 'var(--text)',
      color: 'white'
    },
    botonSuccess: {
      backgroundColor: 'var(--brand-success)',
      color: 'white'
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
      borderBottom: '1px solid var(--divider)',
      color: 'var(--text)'
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
      transition: 'all 0.2s ease',
      fontWeight: '500'
    },
    botonTransicion: {
      backgroundColor: 'var(--brand-success)',
      color: 'white'
    },
    mensajeCargando: {
      textAlign: 'center',
      padding: '20px',
      color: 'var(--text)',
      fontSize: '1.1em'
    },
    mensajeVacio: {
      textAlign: 'center',
      padding: '30px',
      color: 'var(--text)',
      fontSize: '1em'
    },
    modal: {
      position: 'fixed',
      inset: '0',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      zIndex: '1000'
    },
    modalContenido: {
      backgroundColor: 'var(--surface)',
      padding: '30px',
      borderRadius: '8px',
      width: '100%',
      maxWidth: '500px',
      boxShadow: 'var(--shadow)',
      color: 'var(--text-h)'
    },
    modalTitulo: {
      fontSize: '1.3em',
      fontWeight: '600',
      marginBottom: '20px',
      color: 'var(--text-h)'
    },
    estadoPendiente: {
      fontWeight: '600',
      color: 'var(--brand-warning)'
    },
    estadoRuta: {
      fontWeight: '600',
      color: 'var(--brand-primary)'
    },
    estadoEntregado: {
      fontWeight: '600',
      color: 'var(--brand-success)'
    }
  }

  const getEstadoStyle = (estado) => {
    switch(estado) {
      case 'PENDIENTE': return estilos.estadoPendiente
      case 'EN_RUTA': return estilos.estadoRuta
      case 'ENTREGADO': return estilos.estadoEntregado
      default: return {}
    }
  }

  return (
    <div style={estilos.seccion}>
      <h2 style={estilos.titulo}>Crear Nueva Guía de Despacho</h2>

      <form onSubmit={crearEnvio} style={estilos.formulario}>
        <div style={estilos.grupoInputs}>
          <div style={estilos.grupo}>
            <label style={estilos.etiqueta}>Pedido ID *</label>
            <input
              type="number"
              name="pedidoId"
              value={form.pedidoId}
              onChange={manejarCambio}
              placeholder="Ej: 1"
              style={estilos.entrada}
              min="1"
            />
          </div>
          <div style={estilos.grupo}>
            <label style={estilos.etiqueta}>Transportista *</label>
            <select
              name="transportistaId"
              value={form.transportistaId}
              onChange={manejarCambio}
              style={estilos.select}
            >
              <option value="">Selecciona un transportista</option>
              {transportistas.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.nombre} ({t.patente})
                </option>
              ))}
            </select>
          </div>
        </div>

        <div style={estilos.grupo}>
          <label style={estilos.etiqueta}>Dirección de Entrega *</label>
          <input
            type="text"
            name="direccionEntrega"
            value={form.direccionEntrega}
            onChange={manejarCambio}
            placeholder="Ej: Calle Principal 123, Apto 5"
            style={{...estilos.entrada, minHeight: '60px', padding: '10px'}}
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
            {cargando ? 'Creando...' : 'Crear Guía'}
          </button>
        </div>
      </form>

      <hr style={{ margin: '30px 0', border: 'none', borderTop: '1px solid var(--divider)' }} />

      <h2 style={{ ...estilos.titulo, marginTop: '30px' }}>Listado de Envíos</h2>

      {cargando ? (
        <div style={estilos.mensajeCargando}>Cargando envíos...</div>
      ) : envios.length === 0 ? (
        <div style={estilos.mensajeVacio}>No hay envíos registrados en el sistema</div>
      ) : (
        <table style={estilos.tabla}>
          <thead>
            <tr>
              <th style={estilos.th}>ID</th>
              <th style={estilos.th}>Pedido</th>
              <th style={estilos.th}>Transportista</th>
              <th style={estilos.th}>Dirección</th>
              <th style={estilos.th}>Estado</th>
              <th style={estilos.th}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {envios.map((e, índice) => (
              <tr
                key={e.id}
                style={índice % 2 === 0 ? estilos.filaPar : estilos.filaImpar}
              >
                <td style={estilos.td}><strong>{e.id}</strong></td>
                <td style={estilos.td}>{e.pedidoId ?? e.pedido_id ?? '-'}</td>
                <td style={estilos.td}>{e.transportistaNombre ?? e.transportista_nombre ?? e.transportistaId}</td>
                <td style={estilos.td}>{e.direccionEntrega ?? e.direccion_entrega}</td>
                <td style={{
                  ...estilos.td,
                  ...getEstadoStyle(e.estado)
                }}>
                  {e.estado}
                </td>
                <td style={estilos.td}>
                  <div style={estilos.accionesTabla}>
                    {(e.estado === 'PENDIENTE' || e.estado === 'EN_RUTA') && (
                      <button
                        style={{
                          ...estilos.botonTabla,
                          ...estilos.botonTransicion
                        }}
                        onClick={() => avanzarEstado(e.id, e.estado)}
                        onMouseOver={(e) => e.target.style.backgroundColor = 'var(--brand-success)'}
                        onMouseOut={(e) => e.target.style.backgroundColor = 'var(--brand-success)'}
                        disabled={cargando}
                      >
                        {e.estado === 'PENDIENTE' ? 'Poner en ruta' : 'Marcar entregado'}
                      </button>
                    )}
                    {e.estado === 'ENTREGADO' && (
                      <span style={{ ...estilos.botonTabla, backgroundColor: 'var(--surface-muted)', cursor: 'default' }}>
                        Completado
                      </span>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}


    </div>
  )
}
