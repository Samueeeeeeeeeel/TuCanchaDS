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
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.BookingRepository
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.BookingViewModel
import com.example.proyectocancha.ui.viewmodel.BookingViewModelFactory
import com.example.proyectocancha.ui.viewmodel.CanchaViewModel
import com.example.proyectocancha.ui.viewmodel.CanchaViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetalleReservaScreen(navController: NavHostController, courtId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val courtRepo = remember { CourtRepository(db.courtDao()) }
    val canchaVm: CanchaViewModel = viewModel(factory = CanchaViewModelFactory(courtRepo))
    val canchaState by canchaVm.state.collectAsStateWithLifecycle()

    val bookingRepo = remember { BookingRepository(db.bookingDao()) }
    val bookingViewModel: BookingViewModel = viewModel(factory = BookingViewModelFactory(bookingRepo))
    val bookingState by bookingViewModel.state.collectAsStateWithLifecycle()
    val bookingResult by bookingViewModel.bookingResult.collectAsStateWithLifecycle()

    LaunchedEffect(courtId) {
        canchaVm.loadCourtById(courtId)
    }

    val court = canchaState.selectedCourt
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
            bookingViewModel.loadAvailableTimes(courtId, newDate)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

    val pricePerHour = court?.price ?: 0.0
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    LaunchedEffect(bookingResult) {
        bookingResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "¡Reserva confirmada con éxito!", Toast.LENGTH_LONG).show()
                navController.navigate(Routes.reciboReserva.path) { popUpTo(Routes.principal.path) }
                bookingViewModel.clearResult()
            } else {
                Toast.makeText(context, "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                bookingViewModel.clearResult()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(court?.name ?: "Cargando...", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            if (court != null) {
                Button(
                    onClick = {
                        selectedDate?.let { date ->
                            selectedTime?.let { time ->
                                bookingViewModel.addBooking(court.id, court.name, date, time, pricePerHour)
                            }
                        }
                    },
                    enabled = selectedDate != null && selectedTime != null,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Confirmar Reserva (${clpFormatter.format(pricePerHour)})", fontSize = 18.sp)
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        if (canchaState.isLoading || court == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(court.name, color = LightGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(court.description, color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. Selecciona un día", color = LightGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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

                // --- ¡LÓGICA COMPLETADA AQUÍ! ---
                if (selectedDate != null) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("2. Selecciona una hora", color = LightGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            if (bookingState.isLoading) {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else if (bookingState.availableTimes.isEmpty()) {
                                Text("No hay horas disponibles para este día.", color = Color.Gray)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bookingState.availableTimes.forEach { time ->
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
