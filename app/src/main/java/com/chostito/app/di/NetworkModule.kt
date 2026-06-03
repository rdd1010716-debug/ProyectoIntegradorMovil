package com.chostito.app.di

import com.chostito.app.data.local.SessionManager
import com.chostito.app.data.remote.ApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        sessionManager: SessionManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")

                sessionManager.jwtToken?.let { token ->
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                if (original.body != null && original.header("Content-Type") == null) {
                    requestBuilder.header("Content-Type", "application/json")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApiProvider(okHttpClient: OkHttpClient): ApiProvider {
        return ApiProvider(okHttpClient)
    }
}
