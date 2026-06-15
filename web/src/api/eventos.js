import { supabase } from './supabase'
import { keysToCamelCase } from './helpers'

export const eventosApi = {
  getAll: async (params = {}) => {
    let query = supabase
      .from('eventos')
      .select('*, categorias(nombre), lugares(*)')
    
    if (params.estado) query = query.eq('estado', params.estado)
    
    const { data, error } = await query
    if (error) throw error
    
    return data.map(e => keysToCamelCase({
      ...e,
      categoria: e.categorias?.nombre,
      lugar: e.lugares?.nombre,
      ciudad: e.lugares?.ciudad,
      pais: e.lugares?.pais,
    }))
  },

  getById: async (id) => {
    const { data, error } = await supabase
      .from('eventos')
      .select('*, categorias(nombre), lugares(*)')
      .eq('id', id)
      .single()
    if (error) throw error
    
    return keysToCamelCase({
      ...data,
      categoria: data.categorias?.nombre,
      lugar: data.lugares?.nombre,
      ciudad: data.lugares?.ciudad,
      pais: data.lugares?.pais,
    })
  },

  getEntradas: async (id) => {
    const { data, error } = await supabase
      .from('entradas')
      .select('*')
      .eq('id_evento', id)
    if (error) throw error
    return keysToCamelCase(data)
  },

  getAsientos: async (id) => {
    const { data: entradas, error } = await supabase
      .from('entradas')
      .select('id, tipo')
      .eq('id_evento', id)
      .eq('tipo', 'VIP')
    if (error) throw error
    if (!entradas.length) return []
    
    const entradaId = entradas[0].id
    const { data: asientos, error: asientosError } = await supabase
      .from('asientos')
      .select('*')
      .eq('id_entrada', entradaId)
      .order('numero')
    if (asientosError) throw asientosError
    
    const secciones = {}
    asientos.forEach(a => {
      if (!secciones[a.seccion]) secciones[a.seccion] = []
      secciones[a.seccion].push(a)
    })
    
    return Object.entries(secciones).map(([seccion, asientos]) => ({
      seccion,
      asientos: keysToCamelCase(asientos),
    }))
  },

  misEventos: async () => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data, error } = await supabase
      .from('eventos')
      .select('*, categorias(nombre), lugares(*)')
      .eq('id_organizador', user.id)
    if (error) throw error
    
    return data.map(e => keysToCamelCase({
      ...e,
      categoria: e.categorias?.nombre,
      lugar: e.lugares?.nombre,
      ciudad: e.lugares?.ciudad,
      pais: e.lugares?.pais,
    }))
  },

  getTodos: async () => {
    const { data, error } = await supabase
      .from('eventos')
      .select('*, categorias(nombre), lugares(*)')
    if (error) throw error
    
    return data.map(e => keysToCamelCase({
      ...e,
      categoria: e.categorias?.nombre,
      lugar: e.lugares?.nombre,
      ciudad: e.lugares?.ciudad,
      pais: e.lugares?.pais,
    }))
  },

  create: async (data) => {
    const { data: { user } } = await supabase.auth.getUser()
    const { data: result, error } = await supabase
      .from('eventos')
      .insert({ ...data, id_organizador: user.id })
      .select()
      .single()
    if (error) throw error
    return keysToCamelCase(result)
  },

  update: async (id, data) => {
    const { data: result, error } = await supabase
      .from('eventos')
      .update(data)
      .eq('id', id)
      .select()
      .single()
    if (error) throw error
    return keysToCamelCase(result)
  },

  delete: async (id) => {
    const { error } = await supabase.from('eventos').delete().eq('id', id)
    if (error) throw error
    return { success: true }
  },

  agregarEntradas: async (id, data) => {
    const items = data.map(e => ({ ...e, id_evento: id }))
    const { data: result, error } = await supabase
      .from('entradas')
      .insert(items)
      .select()
    if (error) throw error
    return keysToCamelCase(result)
  },

  reemplazarEntradas: async (id, data) => {
    await supabase.from('entradas').delete().eq('id_evento', id)
    return eventosApi.agregarEntradas(id, data)
  },

  uploadImagen: async (id, formData) => {
    const file = formData.get('imagen')
    if (!file) throw new Error('No se proporciono imagen')
    
    const fileName = `eventos/${id}-${Date.now()}.jpg`
    const { error: uploadError } = await supabase.storage
      .from('eventos')
      .upload(fileName, file)
    if (uploadError) throw uploadError
    
    const { data: { publicUrl } } = supabase.storage
      .from('eventos')
      .getPublicUrl(fileName)
    
    const { error: updateError } = await supabase
      .from('eventos')
      .update({ imagen_url: publicUrl })
      .eq('id', id)
    if (updateError) throw updateError
    
    return { imagenUrl: publicUrl }
  },
}
