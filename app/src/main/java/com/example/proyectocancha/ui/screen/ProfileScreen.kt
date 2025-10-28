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
import com.example.proyectocancha.navigation.Routess // ✅ corregido
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme

@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val mainBg = Grey900
    val cardBg = Color(0xFF333333)
    val textColor = Color.White
    val mutedTextColor = Color.Gray
    val errorColor = Color(0xFFF44336)

    // Datos ficticios
    val userName = "Juan Pérez"
    val userEmail = "juan.perez@example.com"
    val userPhone = "+56 9 8765 4321"

    // Acciones
    val onEditProfile: () -> Unit = { /* TODO */ }
    val onChangePassword: () -> Unit = { /* TODO */ }
    val onLogout: () -> Unit = {
        navController.navigate(Routess.login.path) {
            popUpTo(Routess.principal.path) { inclusive = true }
        }
    }
    val onNotifications: () -> Unit = { /* TODO */ }
    val onPaymentMethods: () -> Unit = { /* TODO */ }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBg)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Información personal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Información Personal",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = userName,
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Correo: $userEmail", color = mutedTextColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Teléfono: $userPhone", color = mutedTextColor, fontSize = 14.sp)
                }
            }
        }

        // Configuración
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
                    SettingItem(Icons.Filled.Notifications, "Notificaciones", onNotifications)
                    Spacer(Modifier.height(8.dp))
                    SettingItem(Icons.Filled.Payment, "Métodos de Pago", onPaymentMethods)
                    Spacer(Modifier.height(8.dp))
                    SettingItem(Icons.Filled.AccountCircle, "Editar Perfil", onEditProfile)
                    Spacer(Modifier.height(8.dp))
                    SettingItem(Icons.Filled.Person, "Cambiar Contraseña", onChangePassword)
                }
            }
        }

        // Botón cerrar sesión
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor),
                border = BorderStroke(1.dp, errorColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar sesión", fontSize = 16.sp)
            }
        }
    }
}

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
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, color = Color.White, fontSize = 16.sp)
        }
        Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Ir a $label", tint = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProyectoCanchaTheme {
        ProfileScreen(navController = rememberNavController())
    }
}