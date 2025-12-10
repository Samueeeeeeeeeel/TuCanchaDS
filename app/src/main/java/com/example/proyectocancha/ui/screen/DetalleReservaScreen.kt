package com.example.proyectocancha.ui.screen

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModelFactory
import com.example.proyectocancha.ui.viewmodel.RemoteReservaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteReservaViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetalleReservaScreen(navController: NavHostController, courtId: Int) {
    val context = LocalContext.current

    // ViewModels para cancha y reserva
    val canchaVm: RemoteCanchaViewModel = viewModel(factory = RemoteCanchaViewModelFactory())
    val reservaVm: RemoteReservaViewModel = viewModel(factory = RemoteReservaViewModelFactory())
    
    val canchaState by canchaVm.state.collectAsStateWithLifecycle()
    val reservaState by reservaVm.state.collectAsStateWithLifecycle()
    val bookingResult by reservaVm.bookingResult.collectAsStateWithLifecycle()

    LaunchedEffect(courtId) {
        canchaVm.loadCanchaById(courtId.toLong())
    }

    val cancha = canchaState.selectedCancha
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val newDate = dateFormat.format(calendar.time)
            selectedDate = newDate
            selectedTime = null // Resetea la hora al cambiar de día
            reservaVm.loadAvailableTimes(courtId.toLong(), newDate)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

    val pricePerHour = cancha?.precioPorHora ?: 0
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    LaunchedEffect(bookingResult) {
        bookingResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "¡Reserva confirmada con éxito!", Toast.LENGTH_LONG).show()
                navController.navigate(Routes.reciboReserva.path) { popUpTo(Routes.principal.path) }
                reservaVm.clearResult()
            } else {
                Toast.makeText(
                    context,
                    "Error: ${it.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
                reservaVm.clearResult()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cancha?.nombre ?: "Cargando...", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            if (cancha != null) {
                Button(
                    onClick = {
                        selectedDate?.let { date ->
                            selectedTime?.let { time ->
                                reservaVm.crearReserva(
                                    canchaId = cancha.id ?: 0L,
                                    fecha = date,
                                    hora = time,
                                    precioPorHora = pricePerHour
                                )
                            }
                        }
                    },
                    enabled = selectedDate != null && selectedTime != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Confirmar Reserva (${clpFormatter.format(pricePerHour)})", fontSize = 18.sp)
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        when {
            canchaState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LightGreen)
                }
            }
            canchaState.errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = canchaState.errorMsg ?: "Error",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { canchaVm.loadCanchaById(courtId.toLong()) },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            cancha == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancha no encontrada", color = Color.Gray)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Info de la cancha
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                cancha.nombre,
                                color = LightGreen,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                cancha.tipo,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            cancha.descripcion?.let { desc ->
                                Text(desc, color = Color.Gray, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Dirección: ${cancha.direccion}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Selector de fecha
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "1. Selecciona un día",
                                color = LightGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(8.dp))
                                    .clickable { datePickerDialog.show() }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedDate ?: "Toca para elegir una fecha",
                                    color = if (selectedDate != null) Color.White else Color.Gray
                                )
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Seleccionar Fecha",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    // Selector de hora (solo se muestra si hay fecha seleccionada)
                    if (selectedDate != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "2. Selecciona una hora",
                                    color = LightGreen,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                if (reservaState.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = LightGreen)
                                    }
                                } else if (reservaState.availableTimes.isEmpty()) {
                                    Text(
                                        "No hay horas disponibles para este día.",
                                        color = Color.Gray
                                    )
                                } else {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        reservaState.availableTimes.forEach { time ->
                                            val isSelected = selectedTime == time
                                            Button(
                                                onClick = { selectedTime = time },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) DarkGreen else Color.Transparent
                                                ),
                                                border = if (isSelected) null else BorderStroke(1.dp, Color.Gray)
                                            ) {
                                                Text(time, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
