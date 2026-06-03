package com.chostito.app.data.remote.dto

data class FavoritoDto(
    val id: Int,
    val fechaAgregado: String? = null,
    val usuarioId: Int? = null,
    val eventoId: Int? = null,
    val evento: EventoDto? = null
)
