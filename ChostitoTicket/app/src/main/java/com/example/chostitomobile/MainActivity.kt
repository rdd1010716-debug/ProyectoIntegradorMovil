package com.example.chostitomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chostitomobile.data.network.SupabaseClient
import com.example.chostitomobile.data.repository.AuthRepository
import com.example.chostitomobile.data.repository.EventoRepository
import com.example.chostitomobile.ui.navigation.AppNavigation
import com.example.chostitomobile.ui.theme.ChostitoMobileTheme
import com.example.chostitomobile.ui.viewmodel.AuthViewModel
import com.example.chostitomobile.ui.viewmodel.EventoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Comentado temporalmente para probar

        val supabaseClient = SupabaseClient(this)
        val authRepository = AuthRepository(supabaseClient)
        val eventoRepository = EventoRepository(supabaseClient)

        setContent {
            ChostitoMobileTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.Factory(authRepository)
                )
                val eventoViewModel: EventoViewModel = viewModel(
                    factory = EventoViewModel.Factory(eventoRepository)
                )

                val user by authViewModel.user

                AppNavigation(
                    authViewModel = authViewModel,
                    eventoViewModel = eventoViewModel,
                    user = user
                )
            }
        }
    }
}
