package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.ui.navigation.Routess
// IMPORTAMOS TUS COLORES
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.DarkGreen

@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    // --- Colores (Según tu proyecto) ---
    val mainBg = Grey900 // Fondo de la pantalla
    val cardBg = Color(0xFF333333) // Gris más claro para las tarjetas
    val textColor = Color.White
    val accentGreen = LightGreen
    val darkTextOnGreen = Grey900
    val mutedTextColor = Color.Gray
    val errorColor = Color(0xFFF44336)

    // --- Datos de Usuario (Ficticios) ---
    val userName = "Juan Pérez"
    val userEmail = "juan.perez@example.com"
    val userPhone = "+56 9 8765 4321"
    val userBirthdate = "15/05/1990"

    // --- Acciones de Navegación ---
    val onEditProfile: () -> Unit = { /* TODO: Implementar */ }
    val onChangePassword: () -> Unit = { /* TODO: Implementar */ }
    val onLogout: () -> Unit = {
        navController.navigate(Routess.login.path) {
            popUpTo(Routess.principal.path) { inclusive = true }
        }
    }
    val onSeeCourts: () -> Unit = { navController.navigate(Routess.verCanchas.path) }
    val onReserve: () -> Unit = { navController.navigate(Routess.verCanchas.path) }
    val onNotifications: () -> Unit = { /* TODO: Implementar */ }
    val onPaymentMethods: () -> Unit = { /* TODO: Implementar */ }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBg)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // --- SECCIÓN DE PERFIL DE USUARIO (EN UNA CAJA) ---
        item {
            Card( // <-- AÑADIDA LA CAJA
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.Start // <-- Alineado a la izquierda
                ) {
                    Text( // <-- Título añadido
                        text = "Información Personal",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Nombre
                    Text(
                        text = userName,
                        color = textColor,
                        fontSize = 20.sp, // Un poco más grande
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Correo, Teléfono, Fecha de Nacimiento
                    Text(text = "Correo: $userEmail", color = mutedTextColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp)) // <-- Spacer añadido
                    Text(text = "Teléfono: $userPhone", color = mutedTextColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp)) // <-- Spacer añadido
                }
            }
        }

        // --- CONFIGURACIÓN ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Configuración",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    SettingItem(
                        icon = Icons.Filled.Notifications,
                        label = "Notificaciones",
                        onClick = onNotifications
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingItem(
                        icon = Icons.Filled.Payment,
                        label = "Métodos de Pago",
                        onClick = onPaymentMethods
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingItem(
                        icon = Icons.Filled.AccountCircle,
                        label = "Editar Perfil",
                        onClick = onEditProfile
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingItem(
                        icon = Icons.Filled.Person,
                        label = "Cambiar Contraseña",
                        onClick = onChangePassword
                    )
                }
            }
        }

        // --- BOTONES "VER CANCHAS" Y "RESERVAR" ---


        // --- BOTÓN CERRAR SESIÓN ---
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor),
                border = BorderStroke(1.dp, errorColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Cerrar sesión", fontSize = 16.sp)
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
private fun SettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, color = Color.White, fontSize = 16.sp)
        }
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Ir a $label",
            tint = Color.Gray
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    Box(modifier = Modifier.background(Grey900)) {
        ProfileScreen(navController = rememberNavController())
    }
}