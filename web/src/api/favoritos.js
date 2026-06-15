import { supabase } from './supabase'
import { keysToCamelCase } from './helpers'

export const favoritosApi = {
  getAll: async () => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase
      .from('favoritos')
      .select(`
        *,
        eventos(*, categorias(nombre), lugares(*))
      `)
      .eq('id_usuario', user.id)
    if (error) throw error
    
    return data.map(f => keysToCamelCase({
      id: f.id,
      evento: {
        ...f.eventos,
        categoria: f.eventos?.categorias?.nombre,
        lugar: f.eventos?.lugares?.nombre,
        ciudad: f.eventos?.lugares?.ciudad,
        pais: f.eventos?.lugares?.pais,
      },
    }))
  },

  agregar: async (eventoId) => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase
      .from('favoritos')
      .insert({ id_usuario: user.id, id_evento: eventoId })
      .select()
      .single()
    if (error) throw error
    return keysToCamelCase(data)
  },

  eliminar: async (eventoId) => {
    const { data: { user } } = await supabase.auth.getUser()
    const { error } = await supabase
      .from('favoritos')
      .delete()
      .eq('id_usuario', user.id)
      .eq('id_evento', eventoId)
    if (error) throw error
    return { success: true }
  },
}
