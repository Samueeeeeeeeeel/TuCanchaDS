package com.example.proyectocancha.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectocancha.ui.theme.BlueGrey900
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme // Importación mantenida
import com.example.proyectocancha.ui.theme.Grey900   // Asegúrate de tener esta importación
import com.example.proyectocancha.ui.theme.LightGreen // Asegúrate de tener esta importación

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onOpenDrawer: () -> Unit,  // Abre el drawer (hamburguesa)
    onGoLogin: () -> Unit     // <-- PARÁMETRO CAMBIADO: Navega a Login
) {
    // CAMBIO CLAVE: Usar CenterAlignedTopAppBar para centrar el título
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // Usar centerAligned...
            containerColor = BlueGrey900// Color de fondo de la barra (como en Cinemark)
        ),

        // 1. TÍTULO (Centrado)
        title = {
            Text(
                text = "TuCancha!", // Título visible
                color = LightGreen,  // Color del título
                fontWeight = FontWeight.Bold
            )
        },

        // 2. ICONO DE NAVEGACIÓN (Izquierda - Hamburguesa)
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) { // Al presionar, abre drawer
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú",
                    tint = Color.White // Color del ícono de hamburguesa
                )
            }
        },

        // 3. ACCIONES (Derecha - Botón Iniciar Sesión)
        actions = {
            Button(
                onClick = onGoLogin, // Acción del botón
                modifier = Modifier
                    .padding(end = 8.dp) // Espacio desde el borde derecho
                    .height(36.dp),     // Altura reducida para que quepa bien

                // Color rojo similar al de la imagen
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen // Un rojo oscuro
                ),

                // Reducimos el padding interno para que el texto se ajuste
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = "VER PERFIL",
                    fontSize = 11.sp, // Fuente más pequeña para que quepa
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    ProyectoCanchaTheme {
        AppTopBar(
            onOpenDrawer = { /* Acción vacía para el Preview */ },
            onGoLogin = { /* Acción vacía para el Preview */ } // <-- PARÁMETRO CAMBIADO
        )
    }
}