package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ListAlt
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
// ❗️ Asegúrate de que tus rutas estén definidas en Routess.kt
// import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavHostController
) {
    val mainBg = Grey900
    val cardBg = Color(0xFF333333)
    val textColor = Color.White
    val accentColor = LightGreen

    Scaffold(
        containerColor = mainBg,
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBg,
                    titleContentColor = textColor
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- SECCIÓN 1: GESTIÓN DE CANCHAS ---
            item {
                Text(
                    text = "Gestión de Canchas",
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        AdminMenuItem(
                            icon = Icons.Default.Edit,
                            label = "Editar Canchas",
                            onClick = {
                                // ❗️ Reemplaza "admin_edit_courts" con tu ruta real de Routess
                                navController.navigate("admin_edit_courts")
                            }
                        )
                        Divider(color = mainBg, thickness = 1.dp)
                        AdminMenuItem(
                            icon = Icons.Default.AddCircle,
                            label = "Crear Nueva Cancha",
                            onClick = {
                                // ❗️ Reemplaza "admin_create_court" con tu ruta real de Routess
                                navController.navigate("admin_create_court")
                            }
                        )
                    }
                }
            }

            // --- SECCIÓN 2: OTRAS GESTIONES ---
            item {
                Text(
                    text = "Gestión General",
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        AdminMenuItem(
                            icon = Icons.Default.ListAlt,
                            label = "Ver Todas las Reservas",
                            onClick = {
                                navController.navigate("admin_all_bookings")
                            }
                        )
                        Divider(color = mainBg, thickness = 1.dp)
                        AdminMenuItem(
                            icon = Icons.Default.Person,
                            label = "Gestionar Usuarios",
                            onClick = {
                                navController.navigate("admin_manage_users")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            Spacer(Modifier.width(16.dp))
            Text(label, color = Color.White, fontSize = 16.sp)
        }
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    ProyectoCanchaTheme {
        AdminScreen(navController = rememberNavController())
    }
}