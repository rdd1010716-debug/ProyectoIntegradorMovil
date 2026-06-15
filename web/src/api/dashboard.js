import { supabase } from './supabase'
import { keysToCamelCase } from './helpers'

export const dashboardApi = {
  getStats: async () => {
    const { data, error } = await supabase.rpc('get_dashboard_stats')
    if (error) throw error
    return keysToCamelCase(data)
  },

  misVentas: async () => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase.rpc('get_mis_ventas', {
      p_id_organizador: user.id,
    })
    if (error) throw error
    return keysToCamelCase(data || [])
  },

  todasGanancias: async () => {
    const { data, error } = await supabase.rpc('get_todas_ganancias')
    if (error) throw error
    return keysToCamelCase(data || [])
  },

  escanearQR: async (codigoQR) => {
    const { data, error } = await supabase.rpc('escanear_qr', {
      p_codigo: codigoQR,
    })
    if (error) throw error
    
    if (!data.valido) {
      throw new Error(data.mensaje || 'Entrada invalida')
    }
    
    return keysToCamelCase(data)
  },
}
