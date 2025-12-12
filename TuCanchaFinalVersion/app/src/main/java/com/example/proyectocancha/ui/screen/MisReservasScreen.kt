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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.data.remote.dto.EstadoReserva
import com.example.proyectocancha.data.remote.dto.ReservaDto
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.viewmodel.RemoteReservaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteReservaViewModelFactory
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(navController: NavHostController) {
    // Usar RemoteReservaViewModel para obtener reservas del BACKEND
    val reservaVm: RemoteReservaViewModel = viewModel(factory = RemoteReservaViewModelFactory())
    val reservaState by reservaVm.state.collectAsStateWithLifecycle()
    val bookingResult by reservaVm.bookingResult.collectAsStateWithLifecycle()
//HOLAAAAAAAA
    // Cargar reservas al iniciar
    LaunchedEffect(Unit) {
        reservaVm.loadReservasUsuario()
    }

    var reservaToCancel by remember { mutableStateOf<ReservaDto?>(null) }

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
        when {
            reservaState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            }
            reservaState.errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = reservaState.errorMsg ?: "Error",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { reservaVm.loadReservasUsuario() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            reservaState.reservasList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes reservas registradas.", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Reservas Activas y Pasadas",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }

                    items(reservaState.reservasList) { reserva ->
                        ReservaCard(
                            reserva = reserva,
                            onCardClick = {},
                            onCancelClick = {
                                if (reserva.estado == EstadoReserva.CONFIRMADA || reserva.estado == EstadoReserva.PENDIENTE) {
                                    reservaToCancel = reserva
                                }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(66.dp)) }
                }
            }
        }
    }

    reservaToCancel?.let { reserva ->
        AlertDialog(
            onDismissRequest = { reservaToCancel = null },
            title = { Text("Cancelar reserva") },
            text = { Text("¿Seguro que quieres cancelar esta reserva?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reserva.id?.let { reservaVm.cancelarReserva(it) }
                        reservaToCancel = null
                    }
                ) {
                    Text("Sí, cancelar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaToCancel = null }) {
                    Text("No", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ReservaCard(
    reserva: ReservaDto,
    onCardClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val cardBg = Color(0xFF333333)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    
    // Parsear fecha para mostrar
    val fechaInicio = try {
        LocalDateTime.parse(reserva.fechaInicio, formatter)
    } catch (e: Exception) {
        null
    }
    
    val fechaStr = fechaInicio?.let {
        "${it.dayOfMonth}/${it.monthValue}/${it.year} ${it.hour}:${it.minute.toString().padStart(2, '0')}"
    } ?: reserva.fechaInicio
    
    val statusColor = when (reserva.estado) {
        EstadoReserva.CONFIRMADA, EstadoReserva.PENDIENTE -> DarkGreen
        EstadoReserva.COMPLETADA -> Color.Gray
        EstadoReserva.CANCELADA -> Color.Red
        else -> Color.LightGray
    }
    
    val statusText: String = when (reserva.estado) {
        EstadoReserva.PENDIENTE -> "Pendiente"
        EstadoReserva.CONFIRMADA -> "Confirmada"
        EstadoReserva.CANCELADA -> "Cancelada"
        EstadoReserva.COMPLETADA -> "Completada"
        else -> reserva.estado?.name ?: "Desconocido"
    }
    
    val isCancellable = reserva.estado == EstadoReserva.CONFIRMADA || reserva.estado == EstadoReserva.PENDIENTE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.clickable(onClick = onCardClick).padding(16.dp)) {
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
                        Icons.Default.Info,
                        "Estado",
                        tint = statusColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Cancha ID: ${reserva.canchaId}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            fechaStr,
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        statusText,
                        color = statusColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        clpFormatter.format(reserva.precioTotal),
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.Red.copy(alpha = 0.5f))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
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
    MisReservasScreen(rememberNavController())
}
