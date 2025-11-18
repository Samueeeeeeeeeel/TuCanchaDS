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
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    ProyectoCanchaTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(navController = navController)
        }
    }
}
