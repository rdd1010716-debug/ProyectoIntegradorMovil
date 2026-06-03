package com.chostito.app.data.repository

import com.chostito.app.data.remote.ApiProvider
import com.chostito.app.data.remote.dto.FavoritoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritoRepository @Inject constructor(
    private val apiProvider: ApiProvider
) {
    suspend fun getFavoritos(): Result<List<FavoritoDto>> {
        return try {
            val response = apiProvider.getApi().getFavoritos()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorito(eventoId: Int): Result<FavoritoDto> {
        return try {
            val response = apiProvider.getApi().addFavorito(eventoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorito(eventoId: Int): Result<Unit> {
        return try {
            val response = apiProvider.getApi().removeFavorito(eventoId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
