package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.navigation.Routes // <-- ¡ERROR CORREGIDO!
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900

@Composable
fun ReciboReservaScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón 1: Ver mis reservas
                Button(
                    onClick = { navController.navigate(Routes.misReservas.path) }, // <-- ¡ERROR CORREGIDO!
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Ver Mis Reservas")
                }

                // Botón 2: Volver al inicio
                OutlinedButton(
                    onClick = {
                        navController.navigate(Routes.principal.path) { // <-- ¡ERROR CORREGIDO!
                            popUpTo(Routes.principal.path) { inclusive = true } // <-- ¡ERROR CORREGIDO!
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Volver al Inicio")
                }
            }
        },
        containerColor = Grey900
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Ícono de check verde
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirmado",
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // 2. Mensaje de confirmación
            Text(
                text = "¡Reserva Confirmada!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tu cancha ha sido reservada con éxito. Puedes ver los detalles en la sección \"Mis Reservas\".",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReciboReservaScreenPreview() {
    ReciboReservaScreen(rememberNavController())
}
