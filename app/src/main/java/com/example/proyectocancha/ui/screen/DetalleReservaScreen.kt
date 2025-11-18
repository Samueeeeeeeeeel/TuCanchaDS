package com.example.proyectocancha.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.BookingRepository
import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.model.dummyCourts
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.viewmodel.BookingViewModel
import com.example.proyectocancha.ui.viewmodel.BookingViewModelFactory
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReservaScreen(
    navController: NavHostController,
    courtId: Int,
    pricePerHour: Int
) {
    val context = LocalContext.current

    val court = dummyCourts.find { it.id == courtId }
    val courtName = court?.name ?: "Cancha desconocida"
    val pricePerHourDouble = court?.price?.toDouble() ?: pricePerHour.toDouble()

    val db = remember { AppDatabase.getInstance(context) }
    val bookingRepo = remember { BookingRepository(db.bookingDao()) }
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(bookingRepo)
    )

    var selectedDate by remember { mutableStateOf("") }

    var startMinutes by remember { mutableStateOf<Int?>(null) }
    var endMinutes by remember { mutableStateOf<Int?>(null) }

    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    var durationHours by remember { mutableStateOf(0.0) }

    LaunchedEffect(startMinutes, endMinutes) {
        val s = startMinutes
        val e = endMinutes
        durationHours = if (s != null && e != null && e > s) {
            (e - s) / 60.0
        } else {
            0.0
        }
    }

    val subtotal = pricePerHourDouble * durationHours
    val fee = subtotal * 0.1
    val total = subtotal + fee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Reserva", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val s = startMinutes
                        val e = endMinutes

                        val datosValidos =
                            selectedDate.isNotBlank() &&
                                    s != null && e != null &&
                                    e > s && durationHours > 0.0

                        if (!datosValidos) return@Button

                        val startH = s!! / 60
                        val startM = s % 60
                        val endH = e!! / 60
                        val endM = e % 60
                        val timeText =
                            "%02d:%02d - %02d:%02d".format(startH, startM, endH, endM)

                        bookingViewModel.createBooking(
                            courtName = courtName,
                            day = selectedDate,
                            time = timeText,
                            subtotal = subtotal,
                            fee = fee,
                            total = total
                        )

                        navController.navigate(Routess.reciboReserva.path)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text(
                        text = "✓  PAGAR Y CONFIRMAR ($${"%.2f".format(total)})",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Volver a la Reserva", color = Color.White)
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen de Reserva",
                        color = DarkGreen,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    FilaResumen("Cancha", courtName)
                    FilaResumen(
                        "Día",
                        if (selectedDate.isBlank()) "Sin fecha seleccionada" else selectedDate
                    )
                    val resumenHora =
                        if (startMinutes == null || endMinutes == null || durationHours <= 0.0)
                            "Sin horario"
                        else
                            "$startTimeText - $endTimeText"
                    FilaResumen("Hora", resumenHora)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Seleccionar Día y Horario",
                        color = DarkGreen,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(12.dp))

                    val calendar = Calendar.getInstance()

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)

                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        selectedDate =
                                            "%02d/%02d/%04d".format(d, m + 1, y)
                                    },
                                    year,
                                    month,
                                    day
                                ).show()
                            },
                        readOnly = true,
                        label = { Text("Día (ej: Miércoles, 25 de Octubre)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = startTimeText,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val now = Calendar.getInstance()
                                val initH = startMinutes?.div(60) ?: now.get(Calendar.HOUR_OF_DAY)
                                val initM = startMinutes?.rem(60) ?: now.get(Calendar.MINUTE)

                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val minutesTotal = hour * 60 + minute
                                        startMinutes = minutesTotal
                                        startTimeText =
                                            "%02d:%02d".format(hour, minute)

                                        val e = endMinutes
                                        if (e != null && e <= minutesTotal) {
                                            endMinutes = null
                                            endTimeText = ""
                                        }
                                    },
                                    initH,
                                    initM,
                                    true
                                ).show()
                            },
                        readOnly = true,
                        label = { Text("Hora de inicio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = endTimeText,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val now = Calendar.getInstance()
                                val initH = endMinutes?.div(60) ?: now.get(Calendar.HOUR_OF_DAY)
                                val initM = endMinutes?.rem(60) ?: now.get(Calendar.MINUTE)

                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val minutesTotal = hour * 60 + minute
                                        val s = startMinutes

                                        if (s == null || minutesTotal > s) {
                                            endMinutes = minutesTotal
                                            endTimeText =
                                                "%02d:%02d".format(hour, minute)
                                        }
                                    },
                                    initH,
                                    initM,
                                    true
                                ).show()
                            },
                        readOnly = true,
                        label = { Text("Hora de término") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    val durationText =
                        if (durationHours <= 0.0) ""
                        else "%.2f".format(durationHours)

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Duración (horas)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FilaResumen("Subtotal", "$${"%.2f".format(subtotal)}")
                    FilaResumen("Comisión (10%)", "$${"%.2f".format(fee)}")
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                    FilaResumen(
                        label = "TOTAL",
                        value = "$${"%.2f".format(total)}",
                        highlight = true
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaResumen(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = if (highlight) 18.sp else 16.sp
        )
        Text(
            text = value,
            color = if (highlight) DarkGreen else Color.White,
            fontSize = if (highlight) 18.sp else 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleReservaScreenPreview() {
    DetalleReservaScreen(
        navController = rememberNavController(),
        courtId = 1,
        pricePerHour = 20000
    )
}
