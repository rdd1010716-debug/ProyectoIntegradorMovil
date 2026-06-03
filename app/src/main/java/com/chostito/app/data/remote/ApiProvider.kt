package com.chostito.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiProvider @Inject constructor(
    private val okHttpClient: okhttp3.OkHttpClient
) {
    private var apiService: ApiService? = null

    fun initialize(baseUrl: String) {
        // Asegurar que termine en /
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        apiService = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun isInitialized(): Boolean = apiService != null

    fun getApi(): ApiService {
        return apiService ?: throw IllegalStateException(
            "ApiProvider no inicializado. Llama initialize(baseUrl) primero."
        )
    }
}
