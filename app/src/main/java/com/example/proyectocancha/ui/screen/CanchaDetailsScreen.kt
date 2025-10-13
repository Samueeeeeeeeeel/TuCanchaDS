package com.example.proyectocancha.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.R // Asegúrate de que tus drawables están aquí
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Promotion
import com.example.proyectocancha.ui.model.dummyCourts
import com.example.proyectocancha.ui.model.dummyPromotions
import com.example.proyectocancha.ui.theme.LightGrayBg




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanchaDetailsScreen(navController: NavHostController, courtId: Int) { // Nombre de la función cambiado

    // 1. Lógica de Datos
    // Busca la cancha usando el ID pasado por navegación
    val court = dummyCourts.find { it.id == courtId } ?: Court(
        id = 0,
        name = "Cancha No Encontrada",
        imageUrl = R.drawable.court_1, // Usar un placeholder
        description = "La cancha seleccionada no existe o no está disponible."
    )

    Scaffold(
        // 2. Barra Superior (TopBar)
        topBar = {
            TopAppBar(
                title = { Text(court.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        // 3. Botón Fijo de Reserva (Bottom Bar)
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* TODO: Navegar a la pantalla de Checkout/Pago */ },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Reservar Ahora ($20/hr)", color = Color.White, fontSize = 18.sp)
                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->

        // 4. Contenido Principal Deslizable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Respeta la TopBar y la BottomBar
                .verticalScroll(rememberScrollState()) // Permite el desplazamiento
        ) {
            // A. Imagen de la Cancha
            Image(
                painter = painterResource(id = court.imageUrl),
                contentDescription = court.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            // B. Detalles e Información
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Text(
                    text = court.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Precio por Hora: $20 USD",
                    color = DarkGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Descripción",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = court.description,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(24.dp))

                // C. Bloque de Disponibilidad (Placeholder)
                Text(
                    text = "Seleccionar Fecha y Hora",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightGrayBg)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Calendario / Selector de Slots (TODO)",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanchaDetailsScreenPreview() {
    // Asegúrate de pasar un ID válido para el preview (Ej: 1)
    CanchaDetailsScreen(rememberNavController(), courtId = 1)
}