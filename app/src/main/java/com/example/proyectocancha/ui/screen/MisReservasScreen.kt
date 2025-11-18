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
import androidx.compose.runtime.*
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
import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.data.local.booking.Booking
import com.example.proyectocancha.data.local.booking.BookingManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(navController: NavHostController) {

    // ✅ Lista compartida en toda la app (no se reinicia al navegar)
    val bookings: List<Booking> = BookingManager.bookings

    // ✅ Reserva que el usuario está intentando cancelar (muestra el diálogo)
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            MisReservasBottomBar {
                navController.navigate(Routess.principal.path) {
                    popUpTo(Routess.principal.path) { inclusive = true }
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                items(bookings, key = { it.id }) { booking ->
                    BookingCard(
                        booking = booking,
                        onCardClick = {
                            // Aquí podrías navegar a detalles si quieres
                        },
                        onCancelClick = {
                            // 👉 Aquí NO cancelamos aún. Mostramos el diálogo:
                            bookingToCancel = booking
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(66.dp))
                }
            }

            // ---------------- DIÁLOGO DE CONFIRMACIÓN ----------------
            bookingToCancel?.let { selected ->
                AlertDialog(
                    onDismissRequest = { bookingToCancel = null },
                    title = { Text("Cancelar reserva") },
                    text = {
                        Text(
                            "¿Seguro que deseas cancelar la reserva de:\n\n" +
                                    "${selected.courtName}\n" +
                                    "${selected.date} | ${selected.time}?"
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // ✅ Ahora sí se cancela de verdad
                                BookingManager.cancelBooking(selected.id)
                                bookingToCancel = null
                            }
                        ) {
                            Text("Sí, cancelar", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { bookingToCancel = null }) {
                            Text("No, volver")
                        }
                    }
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------------------------

@Composable
fun BookingCard(
    booking: Booking,
    onCardClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val cardBg = Color(0xFF333333)
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
                .clickable(onClick = onCardClick)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
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

            if (isCancellable) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancelClick,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            Color.Red.copy(alpha = 0.5f)
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text(
                        "CANCELAR RESERVA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
    MisReservasScreen(rememberNavController())
}
