import { supabase } from './supabase'
import { keysToCamelCase } from './helpers'

export const authApi = {
  login: async (email, password) => {
    const { data, error } = await supabase.auth.signInWithPassword({ email, password })
    if (error) throw error
    
    const { data: perfil } = await supabase
      .from('perfiles')
      .select('*')
      .eq('id', data.user.id)
      .single()
    
    return { token: data.session.access_token, user: keysToCamelCase(perfil) }
  },

  register: async (data) => {
    const { data: authData, error } = await supabase.auth.signUp({
      email: data.email,
      password: data.password,
      options: {
        data: {
          nombre: data.nombre,
          rol: data.rol || 'Cliente',
        },
      },
    })
    if (error) throw error
    
    // Si el trigger no creó el perfil, crearlo manualmente
    if (authData.user) {
      const { data: existing } = await supabase
        .from('perfiles')
        .select('id')
        .eq('id', authData.user.id)
        .maybeSingle()
      
      if (!existing) {
        const { error: insertError } = await supabase.from('perfiles').insert({
          id: authData.user.id,
          email: data.email,
          nombre: data.nombre,
          rol: data.rol || 'Cliente',
          telefono: data.telefono || null,
        })
        if (insertError) {
          console.error('Error al crear perfil manualmente:', insertError)
        }
      }
    }
    
    return authData
  },

  solicitarReset: async (email) => {
    const { error } = await supabase.auth.resetPasswordForEmail(email, {
      redirectTo: `${window.location.origin}/reset-password`,
    })
    if (error) throw error
    return { success: true }
  },

  resetPassword: async (email, token, nuevaPassword) => {
    const { error } = await supabase.auth.updateUser({ password: nuevaPassword })
    if (error) throw error
    return { success: true }
  },

  uploadFoto: async (fotoBase64) => {
    const { data: { user } } = await supabase.auth.getUser()
    if (!user) throw new Error('No autenticado')
    
    const fileName = `fotos/${user.id}-${Date.now()}.jpg`
    const base64 = fotoBase64.split(',')[1]
    const blob = await (await fetch(`data:image/jpeg;base64,${base64}`)).blob()
    
    const { error: uploadError } = await supabase.storage
      .from('avatars')
      .upload(fileName, blob, { contentType: 'image/jpeg' })
    
    if (uploadError) throw uploadError
    
    const { data: { publicUrl } } = supabase.storage
      .from('avatars')
      .getPublicUrl(fileName)
    
    const { error: updateError } = await supabase
      .from('perfiles')
      .update({ foto_url: publicUrl })
      .eq('id', user.id)
    
    if (updateError) throw updateError
    return { foto_url: publicUrl }
  },
}
