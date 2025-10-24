package com.example.proyectocancha.ui.components


import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu // <-- IMPORTADO (para Mis Reservas)
import androidx.compose.material.icons.filled.ArrowForward // <-- IMPORTADO (para Ver Canchas)
import androidx.compose.material.icons.filled.Logout // <-- ¡NUEVO! Importar ícono de Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectocancha.ui.theme.BlueGrey900

// ..


data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<com.example.proyectocancha.ui.components.DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier // Modificador opcional
) {
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier,
        drawerContainerColor = BlueGrey900// Color de fondo
    ) {
        // Recorremos las opciones y pintamos ítems
        items.forEach { item -> // Por cada ítem
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF4CAF50), // Verde para ítem seleccionado
                    unselectedContainerColor = Color.Transparent, // Fondo transparente si no está seleccionado
                    selectedTextColor = Color.White, // Texto blanco cuando está seleccionado
                    unselectedTextColor = Color(0xFF4CAF50), // Texto verde cuando no está seleccionado

                    // --- ¡CAMBIOS AQUÍ! ---
                    selectedIconColor = Color.White,   // Icono blanco (seleccionado)
                    unselectedIconColor = Color.White    // Icono blanco (no seleccionado)
                    // --- FIN DEL CAMBIO ---

                ) // Estilo por defecto
            )
        }
    }
}

// Helper para construir la lista estándar de ítems del drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,   // Acción Home
    onProfile: () -> Unit,  // Acción Perfil
    onVerCanchas: () -> Unit, // <-- AÑADIDO
    onMisReservas: () -> Unit, // <-- AÑADIDO
    onLogout: () -> Unit     // <-- ¡NUEVO! Acción para cerrar sesión
): List<com.example.proyectocancha.ui.components.DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),          // Ítem Home
    DrawerItem("Perfil", Icons.Filled.AccountCircle, onProfile),   // Ítem Perfil
    DrawerItem("Ver Canchas", Icons.Filled.ArrowForward, onVerCanchas), // <-- AÑADIDO
    DrawerItem("Mis Reservas", Icons.Filled.Menu, onMisReservas),     // <-- AÑADIDO
    DrawerItem("Cerrar Sesión", Icons.Filled.Logout, onLogout) // <-- ¡NUEVO! Ítem de Logout
)

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    // Lo llamamos directamente, sin el "ProyectoCanchaTheme { ... }"
    // Le ponemos un fondo para que se vea, ya que tu drawer es transparente
    AppDrawer(
        currentRoute = null,
        items = defaultDrawerItems(
            onHome = { },
            onProfile = { },
            onVerCanchas = { }, // <-- AÑADIDO
            onMisReservas = { },  // <-- AÑADIDO
            onLogout = { }       // <-- ¡NUEVO! Añadido para el preview
        ),
        // Añadimos un fondo al preview para que el texto verde se vea
        modifier = Modifier.background(Color.DarkGray)
    )
}