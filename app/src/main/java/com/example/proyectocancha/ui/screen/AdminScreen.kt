package com.example.proyectocancha.ui.screen

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.AdminViewModel
import com.example.proyectocancha.ui.viewmodel.AdminViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { AppDatabase.getInstance(context) }
    val userRepo = remember { UserRepository(db.userDao()) }
    val courtRepo = remember { CourtRepository(db.courtDao()) }
    val factory = remember { AdminViewModelFactory(application, userRepo, courtRepo) }
    val vm: AdminViewModel = viewModel(factory = factory)

    val state by vm.state.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedContent by remember { mutableStateOf("canchas") }

    var showCourtDialog by remember { mutableStateOf(false) }
    var courtToEdit by remember { mutableStateOf<CourtEntity?>(null) }
    var courtToDelete by remember { mutableStateOf<CourtEntity?>(null) }

    val mainBg = Grey900
    val appBarColor = Color(0xFF212121)
    val cardBg = Color(0xFF333333)
    val accentColor = LightGreen
    val textColor = Color.White

    if (showCourtDialog) {
        AddEditCourtDialog(
            courtToEdit = courtToEdit,
            onDismiss = { showCourtDialog = false },
            onSave = { court, newImageUri ->
                if (courtToEdit == null) {
                    vm.addCourt(court.name, court.price, newImageUri, court.description)
                } else {
                    vm.updateCourt(court, newImageUri)
                }
            }
        )
    }

    courtToDelete?.let {
        DeleteConfirmationDialog(
            itemType = "cancha",
            itemName = it.name,
            onConfirm = { vm.deleteCourt(it); courtToDelete = null },
            onDismiss = { courtToDelete = null }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                selectedContent,
                onUsersClicked = { selectedContent = "users"; scope.launch { drawerState.close() } },
                onCanchasClicked = { selectedContent = "canchas"; scope.launch { drawerState.close() } },
                onLogoutClicked = { navController.navigate(Routes.login.path) { popUpTo(Routes.admin.path) { inclusive = true } } }
            )
        }
    ) {
        Scaffold(
            containerColor = mainBg,
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administrador") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menú", tint = textColor) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = appBarColor, titleContentColor = textColor)
                )
            },
            floatingActionButton = {
                if (selectedContent == "canchas") {
                    FloatingActionButton(onClick = { courtToEdit = null; showCourtDialog = true }, containerColor = accentColor) {
                        Icon(Icons.Default.Add, "Añadir Cancha", tint = Color.Black)
                    }
                }
            }
        ) { innerPadding ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else {
                when (selectedContent) {
                    "users" -> UserList(state.userList, vm::toggleUserAdminStatus, Modifier.padding(innerPadding), cardBg, textColor)
                    "canchas" -> CourtList(state.courtList, { court -> courtToEdit = court; showCourtDialog = true }, { court -> courtToDelete = court }, Modifier.padding(innerPadding), cardBg, textColor)
                }
            }
        }
    }
}

@Composable
fun CourtList(courts: List<CourtEntity>, onEdit: (CourtEntity) -> Unit, onDelete: (CourtEntity) -> Unit, modifier: Modifier = Modifier, cardColor: Color, textColor: Color) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(courts, key = { it.id }) {
            CourtCard(it, onEdit = { onEdit(it) }, onDelete = { onDelete(it) }, cardColor = cardColor, textColor = textColor)
        }
    }
}

@Composable
fun CourtCard(court: CourtEntity, onEdit: () -> Unit, onDelete: () -> Unit, cardColor: Color, textColor: Color) {
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column {
            if (court.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = File(court.imageUrl)),
                    contentDescription = court.name,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, contentDescription = "Sin imagen", tint = Color.LightGray, modifier = Modifier.size(60.dp))
                }
            }
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(court.name, fontWeight = FontWeight.Bold, color = textColor, fontSize = 18.sp)
                    Text("Precio: ${clpFormatter.format(court.price)}", color = textColor.copy(alpha = 0.8f))
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Color.Cyan) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
                }
            }
        }
    }
}

@Composable
fun AddEditCourtDialog(courtToEdit: CourtEntity?, onDismiss: () -> Unit, onSave: (CourtEntity, String?) -> Unit) {
    var name by remember { mutableStateOf(courtToEdit?.name ?: "") }
    var price by remember { mutableStateOf(courtToEdit?.price?.toInt()?.toString() ?: "") }
    var description by remember { mutableStateOf(courtToEdit?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(courtToEdit?.imageUrl?.let { if (File(it).exists()) Uri.fromFile(File(it)) else null }) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUri = it
            } catch (e: SecurityException) {
                // Handle error
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (courtToEdit == null) "Añadir Cancha" else "Editar Cancha") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                    label = { Text("Precio (CLP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })

                Spacer(Modifier.height(8.dp))
                Button(onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }) {
                    Text("Seleccionar Imagen")
                }
                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(model = it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceDouble = price.toDoubleOrNull() ?: 0.0
                val updatedCourt = courtToEdit?.copy(name = name, price = priceDouble, description = description) ?: CourtEntity(name = name, price = priceDouble, description = description, imageUrl = "")
                onSave(updatedCourt, imageUri?.toString())
                onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { Text("Cancelar") } 
        }
    )
}


@Composable
fun AdminDrawerContent(selectedContent: String, onUsersClicked: () -> Unit, onCanchasClicked: () -> Unit, onLogoutClicked: () -> Unit) {
    ModalDrawerSheet(drawerContainerColor = Grey900) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Menú de Admin", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp), color = Color.White)
            AdminDrawerItem(Icons.Default.People, "Ver Usuarios", selectedContent == "users", onUsersClicked)
            AdminDrawerItem(Icons.Default.SportsSoccer, "Ver Canchas", selectedContent == "canchas", onCanchasClicked)
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            AdminDrawerItem(Icons.Default.ExitToApp, "Cerrar Sesión", false, onLogoutClicked)
        }
    }
}

@Composable
fun AdminDrawerItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) LightGreen.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) LightGreen else Color.White
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).background(backgroundColor, RoundedCornerShape(8.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = contentColor)
    }
}

@Composable
fun UserList(users: List<UserEntity>, onAdminToggle: (UserEntity) -> Unit, modifier: Modifier = Modifier, cardColor: Color, textColor: Color) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { user -> UserCard(user, { onAdminToggle(user) }, cardColor = cardColor, textColor = textColor) }
    }
}

@Composable
fun UserCard(user: UserEntity, onAdminToggle: () -> Unit, modifier: Modifier = Modifier, cardColor: Color, textColor: Color) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
            Text(user.email, style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.7f))
            Text(user.phone, style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.7f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Admin: ", color = textColor.copy(alpha = 0.7f))
                Switch(checked = user.isAdmin, onCheckedChange = { onAdminToggle() }, enabled = true, colors = SwitchDefaults.colors(checkedThumbColor = LightGreen))
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(itemType: String, itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar la $itemType \"$itemName\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Eliminar", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
