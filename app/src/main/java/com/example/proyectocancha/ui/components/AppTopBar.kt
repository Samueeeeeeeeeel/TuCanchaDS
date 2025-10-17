package com.example.proyectocancha.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme // Importación mantenida

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onOpenDrawer: () -> Unit, // Abre el drawer (hamburguesa)
    onHome: () -> Unit,       // Navega a Home
    onProfile: () -> Unit,   // Navega a Perfil
) {
    var showMenu by remember { mutableStateOf(false) } // Estado del menú overflow

    // CAMBIO CLAVE: Usar TopAppBar en lugar de CenterAlignedTopAppBar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors( // Usar topAppBarColors
            containerColor = Color(0xFF4CAF50)
        ),
        title = { // Slot del título
            Text(
                text = "TuCancha!", // Título visible, ahora alineado a la izquierda
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { // Ícono a la izquierda (hamburguesa)
            IconButton(onClick = onOpenDrawer) { // Al presionar, abre drawer
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú") // Ícono
            }
        },
        actions = { // Acciones a la derecha (íconos + overflow)
            IconButton(onClick = onHome) { // Ir a Home
                Icon(Icons.Filled.Home, contentDescription = "Home") // Ícono Home
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil")
            }
            IconButton(onClick = { showMenu = true }) { // Abre menú overflow
                Icon(Icons.Filled.MoreVert, contentDescription = "Más") // Ícono 3 puntitos
            }
            DropdownMenu(
                expanded = showMenu, // Si está abierto
                onDismissRequest = { showMenu = false } // Cierra al tocar fuera
            ) {
                DropdownMenuItem( // Opción Home
                    text = { Text("Home") },
                    onClick = { showMenu = false; onHome() }
                )
                DropdownMenuItem( // Opción Perfil
                    text = { Text("Perfil") },
                    onClick = { showMenu = false; onProfile() }
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
            onOpenDrawer = { /* Acciones vacías para el Preview */ },
            onHome = { /* Acciones vacías para el Preview */ },
            onProfile = { /* Acciones vacías para el Preview */ }
        )
    }
}