package com.chostito.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    var jwtToken: String? = null
    var serverUrl: String? = null
    var currentUser: com.chostito.app.data.remote.dto.UserDto? = null

    fun clear() {
        jwtToken = null
        serverUrl = null
        currentUser = null
    }
}
