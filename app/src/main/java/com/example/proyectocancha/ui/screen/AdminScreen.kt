package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// CAMBIOS CRUCIALES AQUÍ: Se corrige la ruta de importación a 'ui.model'
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Reservation
import com.example.proyectocancha.ui.theme.*
import com.example.proyectocancha.ui.viewmodel.AdminViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

enum class AdminTab { DASHBOARD, CANCHAS, RESERVAS }

@Composable
fun AdminScreen(navController: NavController) { // <-- RENOMBRADO A AdminScreen
    val vm: AdminViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableStateOf(AdminTab.DASHBOARD) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var editingCourt by remember { mutableStateOf<Court?>(null) } // Si es null, es 'Agregar'

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "TuCancha! Administración",
                onLogout = { navController.popBackStack() } // Implementar lógica de Logout real
            )
        },
        bottomBar = {
            AdminBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Grey900 // CORRECCIÓN: Usamos Grey900 o el color que sea el fondo principal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                AdminTab.DASHBOARD -> DashboardContent(state.courtList, state.reservationList)
                AdminTab.CANCHAS -> CanchasContent(
                    courtList = state.courtList,
                    onAddCourt = {
                        editingCourt = null
                        showDialog = true
                    },
                    onEditCourt = { court ->
                        editingCourt = court
                        showDialog = true
                    },
                    onDeleteCourt = vm::deleteCourt
                )
                AdminTab.RESERVAS -> ReservasContent(state.reservationList, vm::updateReservationStatus)
            }
        }
    }

    if (showDialog) {
        AddEditCourtDialog(
            courtToEdit = editingCourt,
            onDismiss = { showDialog = false },
            onSave = { name, price ->
                if (editingCourt == null) {
                    vm.addCourt(name, price)
                } else {
                    vm.editCourt(editingCourt!!.id, name, price)
                }
                showDialog = false
            }
        )
    }
}

// --------------------------------------------------------------------------------------
// 1. COMPOSABLES DE LA PANTALLA PRINCIPAL
// --------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(title: String, onLogout: () -> Unit) {
    TopAppBar(
        title = { Text(title, color = MainGreen, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900),
        actions = {
            TextButton(onClick = onLogout) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    )
}

@Composable
fun AdminBottomBar(selectedTab: AdminTab, onTabSelected: (AdminTab) -> Unit) {
    NavigationBar(containerColor = Grey900) {
        AdminTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                icon = {
                    val icon = when (tab) {
                        AdminTab.DASHBOARD -> Icons.Default.KeyboardArrowRight // Usamos un icono de ejemplo
                        AdminTab.CANCHAS -> Icons.Default.Edit
                        AdminTab.RESERVAS -> Icons.Default.KeyboardArrowRight
                    }
                    Icon(icon, contentDescription = tab.name)
                },
                label = { Text(tab.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, fontSize = 10.sp) },
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    selectedTextColor = MainGreen,
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = DarkGrayCard
                )
            )
        }
    }
}

// --------------------------------------------------------------------------------------
// 2. CONTENIDO DE LAS PESTAÑAS
// --------------------------------------------------------------------------------------

@Composable
fun DashboardContent(courtList: List<Court>, reservationList: List<Reservation>) {
    val activeCourts = courtList.size
    val totalReservations = reservationList.size
    val pendingReservations = reservationList.count { it.status == "PENDIENTE" }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Resumen Rápido", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DashboardCard(title = "Canchas Activas", value = activeCourts.toString(), color = MainGreen, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            DashboardCard(title = "Total Reservas", value = totalReservations.toString(), color = Color(0xFFFDD835), modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            DashboardCard(title = "Reservas Pendientes", value = pendingReservations.toString(), color = Color(0xFFE57373), modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Text("Últimas Reservas (Simulación)", style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(DarkGrayCard, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(reservationList.take(5)) { reservation ->
                ReservationItem(reservation = reservation, onUpdateStatus = {_, _ ->})
            }
        }
    }
}

@Composable
fun CanchasContent(
    courtList: List<Court>,
    onAddCourt: () -> Unit,
    onEditCourt: (Court) -> Unit,
    onDeleteCourt: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestión de Canchas", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Button(
                onClick = onAddCourt,
                colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Cancha", tint = Color.Black, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("Añadir", color = Color.Black)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courtList, key = { it.id }) { court ->
                CourtManagementItem(
                    court = court,
                    onEdit = { onEditCourt(court) },
                    onDelete = { onDeleteCourt(court.id) }
                )
            }
        }
    }
}

@Composable
fun ReservasContent(reservationList: List<Reservation>, onUpdateStatus: (Int, String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Reservas Activas y Pasadas", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reservationList, key = { it.id }) { reservation ->
                ReservationItem(reservation = reservation, onUpdateStatus = onUpdateStatus)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// 3. COMPONENTES REUTILIZABLES
// --------------------------------------------------------------------------------------

@Composable
fun DashboardCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp), color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun CourtManagementItem(court: Court, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(court.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    "Precio: $${court.price} USD/hr",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF64B5F6))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE57373))
                }
            }
        }
    }
}

@Composable
fun ReservationItem(reservation: Reservation, onUpdateStatus: (Int, String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Cancha: ${reservation.courtName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    reservation.status,
                    color = when (reservation.status) {
                        "CONFIRMADA" -> MainGreen
                        "PENDIENTE" -> Color(0xFFFDD835)
                        "CANCELADA" -> Color(0xFFE57373)
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("Usuario: ${reservation.userName}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            Text("Hora: ${reservation.time}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))

            if (reservation.status == "PENDIENTE") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onUpdateStatus(reservation.id, "CANCELADA") }) {
                        Text("Rechazar", color = Color(0xFFE57373))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onUpdateStatus(reservation.id, "CONFIRMADA") },
                        colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
                    ) {
                        Text("Confirmar", color = Color.Black)
                    }
                }
            }
        }
    }
}


@Composable
fun AddEditCourtDialog(courtToEdit: Court?, onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var name by rememberSaveable { mutableStateOf(courtToEdit?.name ?: "") }
    var priceText by rememberSaveable { mutableStateOf(courtToEdit?.price?.toString() ?: "") }
    val isEditMode = courtToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Editar Cancha" else "Añadir Nueva Cancha", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Cancha", color = Color.White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Precio por Hora (USD)", color = Color.White.copy(alpha = 0.6f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    if (name.isNotBlank() && price != null && price > 0) {
                        onSave(name, price)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                enabled = name.isNotBlank() && priceText.toDoubleOrNull() != null
            ) {
                Text(if (isEditMode) "Guardar Cambios" else "Añadir", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White)
            }
        },
        containerColor = Grey900 // Color de fondo del diálogo
    )
}
