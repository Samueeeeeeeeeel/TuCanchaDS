package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.navigation.Routess
import androidx.compose.foundation.BorderStroke
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme

// 🔄 NUEVO: usamos BookingManager para guardar la reserva
import com.example.proyectocancha.data.local.booking.Booking
import com.example.proyectocancha.data.local.booking.BookingManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciboReservaScreen(navController: NavHostController) {

    // Datos que estás mostrando en la boleta (de momento fijos)
    val courtName = "Cancha Norte - Pasto Real"
    val bookingDay = "Miércoles, 25 de Octubre"
    val bookingTime = "20:00 - 21:00 (1 hora)"
    val subtotal = 20.00
    val fee = 2.50
    val total = 22.50
    val receiptId = "TXN-7984-ABC12345"
    val userName = "Javier Pérez"

    // 🔄 NUEVO: asegurarnos de agregar la reserva SOLO una vez
    LaunchedEffect(Unit) {
        // Creamos un id nuevo basado en el máximo actual
        val newId = (BookingManager.bookings.maxOfOrNull { it.id } ?: 0) + 1

        BookingManager.addBooking(
            Booking(
                id = newId,
                courtName = courtName,
                date = bookingDay,
                time = bookingTime,
                total = total,
                status = "Activa"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recibo de Reserva", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        },
        bottomBar = {
            ReciboBottomBar(
                onMyBookingsClicked = {
                    navController.navigate(Routess.misReservas.path)
                },
                onFinishClicked = {
                    navController.popBackStack(
                        route = Routess.principal.path,
                        inclusive = false
                    )
                }
            )
        },
        containerColor = Grey900
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SuccessHeader()
            Spacer(Modifier.height(24.dp))
            ReceiptCard(
                courtName = courtName,
                day = bookingDay,
                time = bookingTime,
                userName = userName,
                subtotal = subtotal,
                fee = fee,
                total = total,
                receiptId = receiptId
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SuccessHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Pago Exitoso",
            tint = DarkGreen,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "¡Reserva Confirmada!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Tu cancha ha sido reservada con éxito.",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ReceiptCard(
    courtName: String,
    day: String,
    time: String,
    userName: String,
    subtotal: Double,
    fee: Double,
    total: Double,
    receiptId: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            ReceiptSectionHeader("Detalles de la Boleta", DarkGreen)
            ReceiptRow("Transacción ID", receiptId, isBold = true, color = DarkGreen)
            ReceiptRow("Cliente", userName)

            Divider(
                Modifier.padding(vertical = 10.dp),
                color = Color.Gray.copy(alpha = 0.5f)
            )

            ReceiptSectionHeader("Cancha Reservada", Color.White)
            ReceiptRow("Cancha", courtName, isBold = true)
            ReceiptRow("Día", day)
            ReceiptRow("Hora", time)

            Divider(
                Modifier.padding(vertical = 10.dp),
                color = Color.Gray.copy(alpha = 0.5f)
            )

            ReceiptSectionHeader("Resumen de Pago", Color.White)
            ReceiptRow("Subtotal", "$${"%.2f".format(subtotal)}")
            ReceiptRow("Comisión (2.5%)", "$${"%.2f".format(fee)}")

            Divider(
                Modifier.padding(vertical = 10.dp),
                color = Color.Gray.copy(alpha = 0.5f)
            )

            ReceiptRow(
                "TOTAL PAGADO",
                "$${"%.2f".format(total)}",
                isTotal = true
            )
        }
    }
}

@Composable
fun ReceiptSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isTotal: Boolean = false,
    color: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = color,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Normal
        )
        Text(
            text = value,
            color = if (isTotal) DarkGreen else color,
            fontSize = if (isTotal) 20.sp else 16.sp,
            fontWeight = if (isTotal || isBold) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}

@Composable
fun ReciboBottomBar(onMyBookingsClicked: () -> Unit, onFinishClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Grey900)
            .padding(16.dp)
    ) {
        Button(
            onClick = onMyBookingsClicked,
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Ver Mis Reservas", color = Color.White, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onFinishClicked,
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver al Inicio", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReciboReservaScreenPreview() {
    ProyectoCanchaTheme {
        ReciboReservaScreen(rememberNavController())
    }
}
