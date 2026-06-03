package com.chostito.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.chostito.app.presentation.navigation.ChostitoNavGraph
import com.chostito.app.presentation.theme.ChostitoTheme
import com.chostito.app.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChostitoTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val userRole by authViewModel.userRole.collectAsState()
                val serverConfigured by authViewModel.serverConfigured.collectAsState()

                ChostitoNavGraph(
                    isLoggedIn = isLoggedIn,
                    userRole = userRole,
                    serverConfigured = serverConfigured,
                    onLogout = { authViewModel.logout() }
                )
            }
        }
    }
}
