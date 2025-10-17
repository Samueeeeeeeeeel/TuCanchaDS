package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
// Importaciones de tus colores de tema
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
// Importación de rutas (necesaria para la navegación)
import com.example.proyectocancha.ui.navigation.Routess // Asegúrate de que esta importación existe


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciboReservaScreen(navController: NavHostController) {

    // --- DATOS FINALES DE EJEMPLO ---
    // Estas variables deben existir para que ReceiptCard funcione
    val courtName = "Cancha Norte - Pasto Real"
    val bookingDay = "Miércoles, 25 de Octubre"
    val bookingTime = "20:00 - 21:00 (1 hora)"
    val subtotal = 20.00
    val fee = 2.50
    val total = 22.50
    val receiptId = "TXN-7984-ABC12345"
    val userName = "Javier Pérez"


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
                    // Navega a la nueva pantalla de mis reservas
                    navController.navigate(Routess.misReservas.path)
                },
                onFinishClicked = {
                    // Vuelve a la pantalla principal
                    navController.popBackStack(route = Routess.principal.path, inclusive = false)
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

            // LA LLAMADA A LA FUNCIÓN
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

// ----------------------------------------------------------------------
// DEFINICIÓN DE COMPONENTES AUXILIARES
// ----------------------------------------------------------------------

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
            // Sección de Detalles de la Reserva
            ReceiptSectionHeader(title = "Detalles de la Boleta", color = DarkGreen)
            ReceiptRow(label = "Transacción ID", value = receiptId, isBold = true, color = DarkGreen)
            ReceiptRow(label = "Cliente", value = userName)

            Divider(Modifier.padding(vertical = 10.dp), color = Color.Gray.copy(alpha = 0.5f))

            // Sección de la Cancha y Hora
            ReceiptSectionHeader(title = "Cancha Reservada", color = Color.White)
            ReceiptRow(label = "Cancha", value = courtName, isBold = true)
            ReceiptRow(label = "Día", value = day)
            ReceiptRow(label = "Hora", value = time)

            Divider(Modifier.padding(vertical = 10.dp), color = Color.Gray.copy(alpha = 0.5f))

            // Sección de Resumen de Pago
            ReceiptSectionHeader(title = "Resumen de Pago", color = Color.White)
            ReceiptRow(label = "Subtotal", value = "$${"%.2f".format(subtotal)}")
            ReceiptRow(label = "Comisión (2.5%)", value = "$${"%.2f".format(fee)}")

            Divider(Modifier.padding(vertical = 10.dp), color = Color.Gray.copy(alpha = 0.5f))

            // Total
            ReceiptRow(label = "TOTAL PAGADO", value = "$${"%.2f".format(total)}", isTotal = true)
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
fun ReceiptRow(label: String, value: String, isBold: Boolean = false, isTotal: Boolean = false, color: Color = Color.White) {
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
        // Botón 1: Ir a Mis Reservas (Prioritario)
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

        // Botón 2: Volver al Inicio (Secundario)
        OutlinedButton(
            onClick = onFinishClicked,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Gray)),
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
    ReciboReservaScreen(rememberNavController())
}