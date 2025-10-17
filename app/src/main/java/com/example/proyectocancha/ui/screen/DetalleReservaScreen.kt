package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReservaScreen(navController: NavHostController) { // Nombre de la función principal

    // --- DATOS FIJOS DE EJEMPLO para el resumen ---
    val courtName = "Cancha Norte - Pasto Real"
    val bookingDay = "Miércoles, 25 de Octubre"
    val bookingTime = "20:00 - 21:00 (1 hora)"
    val subtotal = 20.00
    val fee = 2.50
    val total = subtotal + fee


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Reserva", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        // Botones de acción en la parte inferior
        bottomBar = {
            DetalleReservaBottomBar( // Usando el nombre de la función auxiliar
                onPayClicked = { /* TODO: Implementar lógica de pago */ },
                onBackClicked = { navController.popBackStack() },
                totalPrice = total
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

            Text(
                text = "Revisa los detalles antes de pagar",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tarjeta de Resumen de la Reserva
            ResumenDeReservaCard(
                courtName = courtName,
                day = bookingDay,
                time = bookingTime
            )

            Spacer(Modifier.height(24.dp))

            // Resumen de Precios
            ResumenDePrecio(subtotal = subtotal, fee = fee, total = total)
        }
    }
}

// ----------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------------------------

@Composable
fun ResumenDeReservaCard(courtName: String, day: String, time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Gris oscuro
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Resumen de Reserva",
                color = DarkGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FilaDeResumen(label = "Cancha", value = courtName)
            FilaDeResumen(label = "Día", value = day)
            FilaDeResumen(label = "Hora", value = time)
        }
    }
}

@Composable
fun FilaDeResumen(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ResumenDePrecio(subtotal: Double, fee: Double, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Gris oscuro
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            FilaDeResumen(label = "Subtotal (1 hr)", value = "$${"%.2f".format(subtotal)}")
            FilaDeResumen(label = "Comisión de Servicio", value = "$${"%.2f".format(fee)}")

            Divider(Modifier.padding(vertical = 10.dp), color = Color.Gray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total a Pagar",
                    color = DarkGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$${"%.2f".format(total)}",
                    color = DarkGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun DetalleReservaBottomBar(onPayClicked: () -> Unit, onBackClicked: () -> Unit, totalPrice: Double) { // Nombre de la función actualizado
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Grey900)
            .padding(16.dp)
    ) {
        Button(
            onClick = onPayClicked,
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("PAGAR Y CONFIRMAR ($${"%.2f".format(totalPrice)})", color = Color.White, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBackClicked,
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver a la Reserva", color = Color.White)
        }
    }
}

// ----------------------------------------------------------------------
// PREVIEW
// ----------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun DetalleReservaScreenPreview() {
    DetalleReservaScreen(rememberNavController())
}