import { supabase } from './supabase'
import { keysToCamelCase } from './helpers'

export const reservasApi = {
  crearReserva: async (payload) => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase.rpc('crear_reserva', {
      p_id_usuario: user.id,
      p_items: payload.items,
      p_ids_entradas: payload.idsEntradas || null,
    })
    if (error) throw error
    return keysToCamelCase(data)
  },

  misReservas: async () => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase
      .from('reservas')
      .select(`
        *,
        reserva_items(
          *,
          entradas(tipo, id_evento),
          asientos(numero),
          entradas_vendidas(codigo_qr, estado)
        ),
        pagos(*)
      `)
      .eq('id_usuario', user.id)
      .order('fecha_reserva', { ascending: false })
    if (error) throw error
    
    return data.map(r => keysToCamelCase({
      ...r,
      entradas: r.reserva_items.map(ri => {
        const ev = ri.entradas_vendidas?.[0]
        return {
          id: ri.id,
          tipo: ri.entradas?.tipo,
          evento: ri.entradas?.id_evento,
          numeroAsiento: ri.asientos?.numero,
          precio: ri.precio_unitario,
          fechaEvento: r.fecha_reserva,
          estado: r.estado === 'Confirmada' && ev?.estado === 'Activa' ? 'Activa' : 'Inactiva',
          codigoQR: ev?.codigo_qr || null,
        }
      }),
      pago: r.pagos?.[0] ? keysToCamelCase(r.pagos[0]) : null,
    }))
  },

  getById: async (id) => {
    const { data, error } = await supabase
      .from('reservas')
      .select(`
        *,
        reserva_items(
          *,
          entradas(tipo),
          asientos(numero),
          entradas_vendidas(codigo_qr, estado)
        ),
        pagos(*)
      `)
      .eq('id', id)
      .single()
    if (error) throw error
    return keysToCamelCase(data)
  },

  cancelar: async (id) => {
    const { error } = await supabase
      .from('reservas')
      .update({ estado: 'Cancelada' })
      .eq('id', id)
    if (error) throw error
    return { success: true }
  },
}
