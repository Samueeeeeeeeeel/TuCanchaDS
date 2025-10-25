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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}
@Composable // Indica que esta función dibuja UI
fun AppRoot() { // Raíz de la app para separar responsabilidades
    val navController = rememberNavController() // Controlador de navegación
    MaterialTheme { // Provee colores/tipografías Material 3
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general
            AppNavGraph(navController = navController) // Carga el NavHost + Scaffold + Drawer
        }
    }
}

