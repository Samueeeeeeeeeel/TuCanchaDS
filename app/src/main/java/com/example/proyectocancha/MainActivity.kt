// UBICACIÓN: app/src/main/java/com.example/proyectocancha/MainActivity.kt

package com.example.proyectocancha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.navigation.AppNavGraph
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme // Asegúrate de tener tu tema
import com.example.proyectocancha.data.local.database.AppDatabase // Importa tu DB corregida
import com.example.proyectocancha.data.repository.CourtRepositoryImpl
import com.example.proyectocancha.ui.viewmodel.AppViewModelFactory
import com.example.uinavegacion.data.repository.UserRepository

class MainActivity : ComponentActivity() {

    // 1. Inicialización de la Base de Datos y Repositorios (lazy para eficiencia)
    private val appDatabase by lazy { AppDatabase.getInstance(applicationContext) }

    // Repositorios que necesitan el DAO de la DB
    private val userRepository by lazy { UserRepository(appDatabase.userDao()) }
    // Aquí, se debe inyectar el DAO de Court si existe. Por ahora, usaremos la implementación simple:
    private val courtRepository by lazy { CourtRepositoryImpl(/* Pasa CourtDao aquí si lo tienes */) }

    // 2. Creación de la Fábrica Centralizada para ViewModels
    private val appFactory by lazy {
        AppViewModelFactory(
            userRepository = userRepository,
            courtRepository = courtRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 3. Pasamos la Factory a la raíz de Compose
        setContent {
            ProyectoCanchaTheme { // Usa tu tema de aplicación
                AppRoot(appFactory = appFactory)
            }
        }
    }
}

@Composable
fun AppRoot(appFactory: AppViewModelFactory) {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // 4. Pasamos la Factory al NavGraph
            AppNavGraph(navController = navController, appFactory = appFactory)
        }
    }
}