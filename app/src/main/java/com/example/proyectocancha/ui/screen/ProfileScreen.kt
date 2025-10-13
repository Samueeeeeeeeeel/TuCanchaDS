package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.ui.navigation.Routess



@Composable
fun ProfileScreen(
    navController: NavHostController // SÓLO ACEPTAMOS EL NAVCONTROLLER
) {
    // --- Valores Ficticios/Hardcodeados para el diseño ---
    val userName = "Juan Pérez"
    val userEmail = "juan.perez@example.com"
    val userPhone = "+56 9 8765 4321"
    val userBirthdate = "15/05/1990"

    // --- Definición de Acciones de Navegación ---
    val onEditProfile: () -> Unit = { /* TODO: Navegar a Routess.editProfile */ }
    val onChangePassword: () -> Unit = { /* TODO: Navegar a Routess.changePassword */ }
    val onLogout: () -> Unit = { navController.navigate(Routess.login.path) {
        // Limpiar el Back Stack al cerrar sesión
        popUpTo(Routess.principal.path) { inclusive = true }
    }}

    // El onGoHome ya no es necesario, puedes usar navController.navigate(Routess.principal.path)

    val bg = MaterialTheme.colorScheme.background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... RESTO DEL CONTENIDO DE TU PANTALLA ...
        // (Usando userName, userEmail, onEditProfile, onLogout, etc., definidos arriba)

        // Nombre
        Text(
            text = userName,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Email
        Text(text = userEmail, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        // Teléfono
        Text(text = userPhone, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        // Fecha de nacimiento
        Text(text = "Fecha de nacimiento: $userBirthdate", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))

        // Botón Editar perfil
        Button(
            onClick = onEditProfile,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Editar perfil", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Historial de reservas - Título
        Text(
            text = "Historial de Reservas",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 18.sp
        )
        // Aquí podrías poner una lista LazyColumn con los datos de reservas

        Spacer(modifier = Modifier.height(24.dp))

        // Configuración - Título
        Text(
            text = "Configuración",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Botón Cambiar contraseña
        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cambiar contraseña", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Botón Cerrar sesión
        OutlinedButton(
            onClick = onLogout, // Usa la acción de logout definida arriba
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    // Si tu tema se llama 'ProyectoCanchaTheme', úsalo aquí.
    // ProyectoCanchaTheme {
    ProfileScreen(navController = rememberNavController())
    // }
}
