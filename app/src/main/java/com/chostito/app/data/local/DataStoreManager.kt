package com.chostito.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chostito_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_JSON_KEY = stringPreferencesKey("user_json")
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userJson: Flow<String?> = context.dataStore.data.map { it[USER_JSON_KEY] }
    val serverUrl: Flow<String?> = context.dataStore.data.map { it[SERVER_URL_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveUserJson(userJson: String) {
        context.dataStore.edit { it[USER_JSON_KEY] = userJson }
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { it[SERVER_URL_KEY] = url }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
