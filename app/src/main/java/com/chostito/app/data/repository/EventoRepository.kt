package com.chostito.app.data.repository

import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.CategoriaDto
import com.chostito.app.data.remote.dto.EntradaTipoDto
import com.chostito.app.data.remote.dto.EventoDto
import com.chostito.app.domain.model.Categoria
import com.chostito.app.domain.model.Evento
import com.chostito.app.domain.model.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventoRepository @Inject constructor(
    private val apiProvider: ApiProvider
) {
    suspend fun getEventos(categoriaId: Int? = null, busqueda: String? = null): Result<List<Evento>> {
        return try {
            val response = apiProvider.getApi().getEventos(categoriaId = categoriaId, busqueda = busqueda, estado = "Publicado")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al cargar eventos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventoById(id: Int): Result<Evento> {
        return try {
            val response = apiProvider.getApi().getEventoById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Evento no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEntradasEvento(id: Int): Result<List<EntradaTipoDto>> {
        return try {
            val response = apiProvider.getApi().getEntradasEvento(id)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategorias(): Result<List<Categoria>> {
        return try {
            val response = apiProvider.getApi().getCategorias()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error categorías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisEventos(): Result<List<Evento>> {
        return try {
            val response = apiProvider.getApi().getMisEventos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvento(evento: EventoDto): Result<Evento> {
        return try {
            val response = apiProvider.getApi().createEvento(evento)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al crear evento"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
