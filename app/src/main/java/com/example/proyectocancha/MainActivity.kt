package com.example.proyectocancha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.navigation.AppNavGraph
import com.example.proyectocancha.ui.viewmodel.AuthViewModel
import com.example.proyectocancha.ui.viewmodel.AuthViewModelFactory
import com.example.proyectocancha.utilities.seedCourtsIfEmpty

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }

        // Recomendado: pre-poblar canchas al arrancar (se ejecuta una sola vez aquí)
        val db = AppDatabase.getInstance(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            seedCourtsIfEmpty(db)
        }
    }
}

@Composable
fun AppRoot() {
    // ====== Construcción de dependencias (Composition Root) ======
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    // ====== Navegación y UI ======
    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}