import { supabase } from './supabase'

export const pagosApi = {
  getPago: async (reservaId) => {
    const { data, error } = await supabase
      .from('pagos')
      .select('*')
      .eq('id_reserva', reservaId)
      .single()
    if (error) throw error
    return data
  },

  simularPago: async (reservaId, metodo = 'QR') => {
    const { data, error } = await supabase.rpc('simular_pago', {
      p_reserva_id: reservaId,
      p_metodo: metodo,
    })
    if (error) throw error
    return data
  },

  generarQR: async (reservaId) => {
    const { data, error } = await supabase.rpc('generar_qr_pago', {
      p_reserva_id: reservaId,
    })
    if (error) throw error
    return data
  },
}
