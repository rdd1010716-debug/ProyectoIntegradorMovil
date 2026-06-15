import { supabase } from './supabase'

export const categoriasApi = {
  getAll: async () => {
    const { data, error } = await supabase.from('categorias').select('*').order('nombre')
    if (error) throw error
    return data
  },

  create: async (data) => {
    const { data: result, error } = await supabase
      .from('categorias')
      .insert(data)
      .select()
      .single()
    if (error) throw error
    return result
  },

  update: async (id, data) => {
    const { data: result, error } = await supabase
      .from('categorias')
      .update(data)
      .eq('id', id)
      .select()
      .single()
    if (error) throw error
    return result
  },

  delete: async (id) => {
    const { error } = await supabase.from('categorias').delete().eq('id', id)
    if (error) throw error
    return { success: true }
  },
}
