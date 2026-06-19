import { useEffect, useState } from 'react'

const API = import.meta.env.VITE_API_TRANSPORTISTAS_URL || 'http://localhost:8083'

export default function Transportistas() {
  const [transportistas, setTransportistas] = useState([])
  const [cargando, setCargando] = useState(false)
  const [form, setForm] = useState({ nombre: '', patente: '', telefono: '', disponible: true })

  useEffect(() => {
    cargarTransportistas()
  }, [])

  const cargarTransportistas = async () => {
    setCargando(true)
    try {
      const res = await fetch(`${API}/api/transportistas/listar`)
      if (!res.ok) {
        const errorTexto = await res.text().catch(() => 'Sin mensaje')
        console.error(`Error del servidor [Código ${res.status}]:`, errorTexto)
        setTransportistas([])
        throw new Error(`Código ${res.status}`)
      }
      const data = await res.json()
      setTransportistas(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Detalle del error capturado:', err)
      setTransportistas([])
    } finally {
      setCargando(false)
    }
  }

  const manejarCambio = (e) => {
    const { name, value, type, checked } = e.target
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value })
  }

  const registrar = async (e) => {
    e.preventDefault()
    const payload = {
      nombre: form.nombre ? form.nombre.trim() : "",
      patente: form.patente ? form.patente.trim().toUpperCase() : "",
      telefono: form.telefono ? form.telefono.trim() : "",
      disponible: form.disponible === true || form.disponible === 'true'
    }

    // Validación preventiva: si algún campo requerido está vacío, detener
    if (!payload.nombre || !payload.patente || !payload.telefono) {
      alert("Por favor, complete todos los campos requeridos")
      return
    }

    // Validación de teléfono: mínimo 7 dígitos
    if (!/^\d{7,}$/.test(payload.telefono)) {
      alert('El teléfono debe contener al menos 7 dígitos.')
      return
    }

    // Validación de formato de patente chilena: AB-1234 o ABC-1234
    const regexPatente = /^[A-Z]{2,3}-\d{3,4}$/
    if (!regexPatente.test(payload.patente)) {
      alert('La patente debe tener un formato chileno válido (Ejemplo: AB-1234 o ABC-1234).')
      return
    }

    try {
      const res = await fetch(`${API}/api/transportistas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      if (!res.ok) {
        const msgError = await res.text().catch(() => 'Sin mensaje detallado')
        console.error(`Error del Servidor [Código ${res.status}]:`, msgError)
        throw new Error(`Respuesta errónea del servidor: ${res.status}`)
      }
      setForm({ nombre: '', patente: '', telefono: '', disponible: true })
      await cargarTransportistas()
    } catch (err) {
      console.error(err)
      alert('Error al registrar transportista')
    }
  }

  const toggleDisponibilidad = async (id, actual) => {
    try {
      const nuevoEstado = !actual
      const res = await fetch(`${API}/api/transportistas/${id}/disponibilidad?disponible=${nuevoEstado}`, {
        method: 'PUT'
      })
      if (!res.ok) {
        const errorTexto = await res.text().catch(() => 'Sin mensaje')
        console.error(`Error del servidor [Código ${res.status}]:`, errorTexto)
        throw new Error(`Código ${res.status}`)
      }
      await cargarTransportistas()
    } catch (err) {
      console.error('Error al actualizar disponibilidad:', err)
      alert('Error al actualizar disponibilidad')
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
    checkbox: {
      width: '18px',
      height: '18px',
      cursor: 'pointer',
      accentColor: 'var(--brand-primary)'
    },
    checkboxContainer: {
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
      marginTop: '10px'
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
    botonWarning: {
      backgroundColor: 'var(--brand-warning)',
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
    botonToggle: {
      backgroundColor: 'var(--brand-warning)',
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
    disponibleSi: {
      fontWeight: '600',
      color: 'var(--brand-success)'
    },
    disponibleNo: {
      fontWeight: '600',
      color: 'var(--brand-danger)'
    }
  }

  return (
    <div style={estilos.seccion}>
      <h2 style={estilos.titulo}>Registrar Nuevo Transportista</h2>

      <form onSubmit={registrar} style={estilos.formulario}>
        <div style={estilos.grupoInputs}>
          <div style={estilos.grupo}>
            <label style={estilos.etiqueta}>Nombre *</label>
            <input
              type="text"
              name="nombre"
              value={form.nombre}
              onChange={manejarCambio}
              placeholder="Ej: Juan Pérez"
              style={estilos.entrada}
            />
          </div>
          <div style={estilos.grupo}>
            <label style={estilos.etiqueta}>Patente *</label>
            <input
              type="text"
              name="patente"
              value={form.patente}
              onChange={manejarCambio}
              placeholder="Ej: AB-1234"
              style={estilos.entrada}
            />
          </div>
          <div style={estilos.grupo}>
            <label style={estilos.etiqueta}>Teléfono *</label>
            <input
              type="text"
              name="telefono"
              value={form.telefono}
              onChange={manejarCambio}
              placeholder="Ej: 912345678"
              style={estilos.entrada}
            />
          </div>
        </div>

        <div style={estilos.checkboxContainer}>
          <input
            type="checkbox"
            id="disponible"
            name="disponible"
            checked={form.disponible}
            onChange={manejarCambio}
            style={estilos.checkbox}
          />
          <label htmlFor="disponible" style={{ fontWeight: '500', color: 'var(--text-h)', cursor: 'pointer' }}>
            Disponible para transportes
          </label>
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
            {cargando ? 'Registrando...' : 'Registrar Transportista'}
          </button>
        </div>
      </form>

      <hr style={{ margin: '30px 0', border: 'none', borderTop: '1px solid var(--divider)' }} />

      <h2 style={{ ...estilos.titulo, marginTop: '30px' }}>Listado de Transportistas</h2>

      {cargando ? (
        <div style={estilos.mensajeCargando}>Cargando transportistas...</div>
      ) : transportistas.length === 0 ? (
        <div style={estilos.mensajeVacio}>No hay transportistas registrados en el sistema</div>
      ) : (
        <table style={estilos.tabla}>
          <thead>
            <tr>
              <th style={estilos.th}>Nombre</th>
              <th style={estilos.th}>Patente</th>
              <th style={estilos.th}>Teléfono</th>
              <th style={estilos.th}>Disponible</th>
              <th style={estilos.th}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {transportistas.map((t, índice) => (
              <tr
                key={t.id}
                style={índice % 2 === 0 ? estilos.filaPar : estilos.filaImpar}
              >
                <td style={estilos.td}><strong>{t.nombre}</strong></td>
                <td style={estilos.td}>{t.patente}</td>
                <td style={estilos.td}>{t.telefono}</td>
                <td style={{
                  ...estilos.td,
                  ...(t.disponible ? estilos.disponibleSi : estilos.disponibleNo)
                }}>
                  {t.disponible ? '✓ Disponible' : '✗ No disponible'}
                </td>
                <td style={estilos.td}>
                  <div style={estilos.accionesTabla}>
                    <button
                      style={{
                        ...estilos.botonTabla,
                        ...estilos.botonToggle
                      }}
                      onClick={() => toggleDisponibilidad(t.id, t.disponible)}
                      onMouseOver={(e) => e.target.style.backgroundColor = 'var(--brand-warning)'}
                      onMouseOut={(e) => e.target.style.backgroundColor = 'var(--brand-warning)'}
                      disabled={cargando}
                    >
                      {t.disponible ? 'No disponible' : 'Disponible'}
                    </button>
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
