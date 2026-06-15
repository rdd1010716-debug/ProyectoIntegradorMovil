import { supabase } from './supabase'

export const lugaresApi = {
  getAll: async () => {
    const { data, error } = await supabase.from('lugares').select('*').order('nombre')
    if (error) throw error
    return data
  },

  create: async (data) => {
    const { data: result, error } = await supabase
      .from('lugares')
      .insert(data)
      .select()
      .single()
    if (error) throw error
    return result
  },

  update: async (id, data) => {
    const { data: result, error } = await supabase
      .from('lugares')
      .update(data)
      .eq('id', id)
      .select()
      .single()
    if (error) throw error
    return result
  },

  delete: async (id) => {
    const { error } = await supabase.from('lugares').delete().eq('id', id)
    if (error) throw error
    return { success: true }
  },
}
