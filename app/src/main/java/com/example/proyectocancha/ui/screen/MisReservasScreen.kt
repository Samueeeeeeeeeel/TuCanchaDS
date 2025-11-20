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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.BookingRepository
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.viewmodel.BookingViewModel
import com.example.proyectocancha.ui.viewmodel.BookingViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(navController: NavHostController) {

    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val bookingRepo = remember { BookingRepository(db.bookingDao()) }
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(bookingRepo)
    )

    val uiState by bookingViewModel.state.collectAsStateWithLifecycle()

    // Le decimos al ViewModel que cargue las reservas cuando la pantalla se inicia.
    LaunchedEffect(Unit) {
        bookingViewModel.loadAllBookings()
    }

    var bookingToCancel by remember { mutableStateOf<BookingEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            MisReservasBottomBar {
                navController.navigate(Routes.principal.path) { 
                    popUpTo(Routes.principal.path) { inclusive = true } 
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Reservas Activas y Pasadas", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, top = 8.dp))
                }

                if (uiState.bookings.isEmpty()) {
                    item { Text("No tienes reservas registradas.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp)) }
                } else {
                    items(uiState.bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onCardClick = {},
                            onCancelClick = { if (booking.status == "Activa") { bookingToCancel = booking } }
                        )
                    }
                }
                item { Spacer(Modifier.height(66.dp)) }
            }
        }
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("Cancelar reserva") },
            text = { Text("¿Seguro que quieres cancelar la reserva de \"${booking.courtName}\"?") },
            confirmButton = {
                TextButton(onClick = { bookingViewModel.cancelBooking(booking.id); bookingToCancel = null }) {
                    Text("Sí, cancelar", color = Color.Red)
                }
            },
            dismissButton = { TextButton(onClick = { bookingToCancel = null }) { Text("No", color = Color.Gray) } }
        )
    }
}

@Composable
fun BookingCard(booking: BookingEntity, onCardClick: () -> Unit, onCancelClick: () -> Unit) {
    val cardBg = Color(0xFF333333)
    val statusColor = when (booking.status) {
        "Activa" -> DarkGreen
        "Completada" -> Color.Gray
        "Cancelada" -> Color.Red
        else -> Color.LightGray
    }
    val isCancellable = booking.status == "Activa"

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.clickable(onClick = onCardClick).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Info, "Estado", tint = statusColor, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(booking.courtName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${booking.day} | ${booking.time}", color = Color.LightGray, fontSize = 14.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(booking.status, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("$${booking.total}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                }
            }
            if (isCancellable) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onCancelClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red), border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red.copy(alpha = 0.5f))), modifier = Modifier.fillMaxWidth().height(38.dp)) {
                    Text("CANCELAR RESERVA", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MisReservasBottomBar(onHomeClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(Grey900).padding(16.dp)) {
        Button(onClick = onHomeClicked, colors = ButtonDefaults.buttonColors(containerColor = DarkGreen), modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Volver al Inicio", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisReservasScreenPreview() {
    MisReservasScreen(rememberNavController())
}
