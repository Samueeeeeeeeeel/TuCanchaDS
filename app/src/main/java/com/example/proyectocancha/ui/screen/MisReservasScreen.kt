package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
// Importaciones de rutas (necesaria para la navegación al inicio)
import com.example.proyectocancha.ui.navigation.Routess
// Importaciones de tus colores de tema
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900


// ----------------------------------------------------------------------
// MODELO DE DATOS
// ----------------------------------------------------------------------

data class Booking(
    val id: Int,
    val courtName: String,
    val date: String,
    val time: String,
    val total: Double,
    val status: String // "Activa", "Completada", "Cancelada"
)

val dummyBookings = listOf(
    Booking(1, "Cancha Norte - Pasto Real", "Hoy, 17 Oct", "20:00 - 21:00", 22.50, "Activa"),
    Booking(2, "Cancha Sur - Sintético", "Sab, 28 Oct", "10:00 - 11:00", 22.50, "Activa"),
    Booking(3, "Cancha Pick - Baby Fut", "Dom, 1 Oct", "18:00 - 19:00", 15.00, "Completada"),
    Booking(4, "Cancha Centro - Tierra", "Lun, 30 Sep", "21:00 - 22:00", 20.00, "Cancelada"),
)


// ----------------------------------------------------------------------
// PANTALLA PRINCIPAL
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        // Botón fijo en la parte inferior: Volver al Inicio
        bottomBar = {
            MisReservasBottomBar {
                // Navega a la pantalla principal
                navController.navigate(Routess.principal.path) {
                    // Limpia la pila para que 'atrás' no vuelva a las reservas
                    popUpTo(Routess.principal.path) { inclusive = true }
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Reservas Activas y Pasadas",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }
            items(dummyBookings) { booking ->
                BookingCard(
                    booking = booking,
                    onCardClick = {
                        // TODO: Navegar a los detalles de la reserva específica
                        println("Ver detalles de la reserva ID: ${booking.id}")
                    },
                    onCancelClick = {
                        // TODO: Implementar lógica de cancelación (mostrar diálogo, llamar API)
                        println("Solicitar cancelación de la reserva ID: ${booking.id}")
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(66.dp)) // Espacio extra para el BottomBar
            }
        }
    }
}

// ----------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------------------------

@Composable
fun BookingCard(booking: Booking, onCardClick: () -> Unit, onCancelClick: () -> Unit) {
    val cardBg = Color(0xFF333333) // Gris oscuro para la tarjeta
    val statusColor = when (booking.status) {
        "Activa" -> DarkGreen
        "Completada" -> Color.Gray
        "Cancelada" -> Color.Red
        else -> Color.LightGray
    }

    val isCancellable = booking.status == "Activa"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onCardClick) // Tarjeta clickable
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Columna de Ícono e Información Principal
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Estado",
                        tint = statusColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = booking.courtName,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${booking.date} | ${booking.time}",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }

                // Columna de Precio y Estado
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = booking.status,
                        color = statusColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$${"%.2f".format(booking.total)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Botón de Cancelar (solo si está activa)
            if (isCancellable) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancelClick,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red.copy(alpha = 0.5f))),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text("CANCELAR RESERVA", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MisReservasBottomBar(onHomeClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Grey900)
            .padding(16.dp)
    ) {
        Button(
            onClick = onHomeClicked,
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver al Inicio", color = Color.White, fontSize = 18.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MisReservasScreenPreview() {
    // Es crucial que Routess esté accesible para el preview si se necesita.
    MisReservasScreen(rememberNavController())
}