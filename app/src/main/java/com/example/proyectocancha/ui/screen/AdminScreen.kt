package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectocancha.data.remote.dto.CanchaDto
import com.example.proyectocancha.data.remote.dto.UsuarioDto
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModelFactory
import com.example.proyectocancha.ui.viewmodel.RemoteUserViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteUserViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    // ViewModels
    val canchaVm: RemoteCanchaViewModel = viewModel(factory = RemoteCanchaViewModelFactory())
    val userVm: RemoteUserViewModel = viewModel(factory = RemoteUserViewModelFactory())
    
    val canchaState by canchaVm.state.collectAsStateWithLifecycle()
    val userState by userVm.state.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedContent by remember { mutableStateOf("canchas") }

    // Estados para diálogos
    var showCanchaDialog by remember { mutableStateOf(false) }
    var canchaToEdit by remember { mutableStateOf<CanchaDto?>(null) }
    var canchaToDelete by remember { mutableStateOf<CanchaDto?>(null) }
    var usuarioToDelete by remember { mutableStateOf<UsuarioDto?>(null) }
    var usuarioToChangeRole by remember { mutableStateOf<UsuarioDto?>(null) }

    val mainBg = Grey900
    val appBarColor = Color(0xFF212121)
    val cardBg = Color(0xFF333333)
    val accentColor = LightGreen
    val textColor = Color.White

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        canchaVm.loadTodasLasCanchas()
    }

    LaunchedEffect(selectedContent) {
        when (selectedContent) {
            "canchas" -> canchaVm.loadTodasLasCanchas()
            "users" -> userVm.loadTodosLosUsuarios()
        }
    }

    // Diálogo para crear/editar cancha
    if (showCanchaDialog) {
        AddEditCanchaDialog(
            canchaToEdit = canchaToEdit,
            onDismiss = { 
                showCanchaDialog = false
                canchaToEdit = null
            },
            onSave = { cancha ->
                if (canchaToEdit == null) {
                    canchaVm.crearCancha(cancha)
                } else {
                    cancha.id?.let { canchaVm.actualizarCancha(it, cancha) }
                }
                showCanchaDialog = false
                canchaToEdit = null
            }
        )
    }

    // Diálogo de confirmación para eliminar cancha
    canchaToDelete?.let { cancha ->
        DeleteConfirmationDialog(
            itemType = "cancha",
            itemName = cancha.nombre,
            onConfirm = {
                cancha.id?.let { canchaVm.eliminarCancha(it) }
                canchaToDelete = null
            },
            onDismiss = { canchaToDelete = null }
        )
    }
    
    // Diálogo de confirmación para eliminar usuario
    usuarioToDelete?.let { usuario ->
        DeleteConfirmationDialog(
            itemType = "usuario",
            itemName = "${usuario.nombre} ${usuario.apellido ?: ""}".trim(),
            onConfirm = {
                usuario.id?.let { userVm.eliminarUsuario(it) }
                usuarioToDelete = null
            },
            onDismiss = { usuarioToDelete = null }
        )
    }
    
    // Diálogo para cambiar rol
    usuarioToChangeRole?.let { usuario ->
        ChangeRoleDialog(
            usuario = usuario,
            onDismiss = { usuarioToChangeRole = null },
            onConfirm = { nuevoRol ->
                usuario.id?.let { userVm.cambiarRolUsuario(it, nuevoRol) }
                usuarioToChangeRole = null
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                selectedContent,
                onUsersClicked = { 
                    selectedContent = "users"
                    scope.launch { drawerState.close() } 
                },
                onCanchasClicked = { 
                    selectedContent = "canchas"
                    scope.launch { drawerState.close() } 
                },
                onLogoutClicked = { 
                    navController.navigate(Routes.login.path) { 
                        popUpTo(Routes.admin.path) { inclusive = true } 
                    } 
                }
            )
        }
    ) {
        Scaffold(
            containerColor = mainBg,
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administrador") },
                    navigationIcon = { 
                        IconButton(onClick = { scope.launch { drawerState.open() } }) { 
                            Icon(Icons.Default.Menu, "Menú", tint = textColor) 
                        } 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = appBarColor, 
                        titleContentColor = textColor
                    )
                )
            },
            floatingActionButton = {
                if (selectedContent == "canchas") {
                    FloatingActionButton(
                        onClick = { 
                            canchaToEdit = null
                            showCanchaDialog = true 
                        },
                        containerColor = accentColor
                    ) {
                        Icon(Icons.Default.Add, "Añadir Cancha", tint = Color.Black)
                    }
                }
            }
        ) { innerPadding ->
            when (selectedContent) {
                "users" -> {
                    UserListAdmin(
                        userState = userState,
                        onReload = { userVm.loadTodosLosUsuarios() },
                        onDelete = { usuario ->
                            usuarioToDelete = usuario
                        },
                        onChangeRole = { usuario ->
                            usuarioToChangeRole = usuario
                        },
                        modifier = Modifier.padding(innerPadding),
                        cardColor = cardBg,
                        textColor = textColor
                    )
                }
                "canchas" -> {
                    CanchaListAdmin(
                        canchaState = canchaState,
                        onEdit = { cancha ->
                            canchaToEdit = cancha
                            showCanchaDialog = true
                        },
                        onDelete = { cancha ->
                            canchaToDelete = cancha
                        },
                        onReload = { canchaVm.loadTodasLasCanchas() },
                        modifier = Modifier.padding(innerPadding),
                        cardColor = cardBg,
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun CanchaListAdmin(
    canchaState: com.example.proyectocancha.ui.viewmodel.RemoteCanchaState,
    onEdit: (CanchaDto) -> Unit,
    onDelete: (CanchaDto) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color,
    textColor: Color
) {
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    
    when {
        canchaState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightGreen)
            }
        }
        canchaState.errorMsg != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
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
                        onClick = onReload,
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
        canchaState.canchasList.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No hay canchas disponibles",
                        color = textColor,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onReload,
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                    ) {
                        Text("Recargar")
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(canchaState.canchasList, key = { it.id ?: 0L }) { cancha ->
                    CanchaCardAdmin(
                        cancha = cancha,
                        onEdit = { onEdit(cancha) },
                        onDelete = { onDelete(cancha) },
                        cardColor = cardColor,
                        textColor = textColor,
                        clpFormatter = clpFormatter
                    )
                }
            }
        }
    }
}

@Composable
fun CanchaCardAdmin(
    cancha: CanchaDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    cardColor: Color,
    textColor: Color,
    clpFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Imagen de la cancha o icono por defecto
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = LightGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!cancha.imagenUrl.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = cancha.imagenUrl,
                            contentDescription = cancha.nombre,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = LightGreen,
                                    strokeWidth = 2.dp
                                )
                            },
                            error = {
                                Icon(
                                    Icons.Default.SportsSoccer,
                                    contentDescription = cancha.tipo,
                                    tint = LightGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        )
                    } else {
                        Icon(
                            Icons.Default.SportsSoccer,
                            contentDescription = cancha.tipo,
                            tint = LightGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        cancha.nombre,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 18.sp
                    )
                    Text(
                        "Tipo: ${cancha.tipo}",
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "Precio: ${clpFormatter.format(cancha.precioPorHora)}/hora",
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "Dirección: ${cancha.direccion}",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    cancha.ciudad?.let {
                        Text(
                            "Ciudad: $it",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Estado: ${if (cancha.activa == true) "Activa" else "Inactiva"}",
                        color = if (cancha.activa == true) LightGreen else Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row {
                    IconButton(onClick = onEdit) { 
                        Icon(Icons.Default.Edit, "Editar", tint = Color.Cyan) 
                    }
                    IconButton(onClick = onDelete) { 
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) 
                    }
                }
            }
            cancha.descripcion?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    desc,
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AddEditCanchaDialog(
    canchaToEdit: CanchaDto?,
    onDismiss: () -> Unit,
    onSave: (CanchaDto) -> Unit
) {
    var nombre by remember { mutableStateOf(canchaToEdit?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(canchaToEdit?.descripcion ?: "") }
    var tipo by remember { mutableStateOf(canchaToEdit?.tipo ?: "Fútbol") }
    var precio by remember { mutableStateOf(canchaToEdit?.precioPorHora?.toString() ?: "") }
    var direccion by remember { mutableStateOf(canchaToEdit?.direccion ?: "") }
    var ciudad by remember { mutableStateOf(canchaToEdit?.ciudad ?: "") }
    var imagenUrl by remember { mutableStateOf(canchaToEdit?.imagenUrl ?: "") }
    var activa by remember { mutableStateOf(canchaToEdit?.activa ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (canchaToEdit == null) "Añadir Cancha" else "Editar Cancha") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo (Fútbol, Tenis, etc.)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { if (it.all { char -> char.isDigit() }) precio = it },
                    label = { Text("Precio por Hora (número entero)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL de Imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://example.com/imagen.jpg") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activa: ", modifier = Modifier.weight(1f))
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val precioInt = precio.toIntOrNull() ?: 0
                    val nuevaCancha = CanchaDto(
                        id = canchaToEdit?.id,
                        nombre = nombre,
                        descripcion = descripcion,
                        tipo = tipo,
                        precioPorHora = precioInt,
                        direccion = direccion,
                        ciudad = ciudad.ifEmpty { null },
                        imagenUrl = imagenUrl.ifEmpty { null },
                        activa = activa
                    )
                    onSave(nuevaCancha)
                },
                enabled = nombre.isNotBlank() && tipo.isNotBlank() && direccion.isNotBlank() && precio.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UserListAdmin(
    userState: com.example.proyectocancha.ui.viewmodel.RemoteUserState,
    onReload: () -> Unit,
    onDelete: (UsuarioDto) -> Unit,
    onChangeRole: (UsuarioDto) -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color,
    textColor: Color
) {
    when {
        userState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightGreen)
            }
        }
        userState.errorMsg != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = userState.errorMsg ?: "Error",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onReload,
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
        userState.usuariosList.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No hay usuarios",
                        color = textColor,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onReload,
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                    ) {
                        Text("Recargar")
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userState.usuariosList, key = { it.id ?: 0L }) { usuario ->
                    UserCardAdmin(
                        usuario = usuario,
                        onDelete = { onDelete(usuario) },
                        onChangeRole = { onChangeRole(usuario) },
                        cardColor = cardColor,
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun UserCardAdmin(
    usuario: UsuarioDto,
    onDelete: () -> Unit,
    onChangeRole: () -> Unit,
    cardColor: Color,
    textColor: Color
) {
    // Verificar si es el admin predeterminado
    val isDefaultAdmin = usuario.email.equals("Admin@admin.cl", ignoreCase = true)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${usuario.nombre} ${usuario.apellido ?: ""}".trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.7f)
                    )
                    usuario.telefono?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Rol: ",
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Text(
                            usuario.rol ?: "USUARIO",
                            color = if (usuario.rol == "ADMIN") LightGreen else textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Estado: ",
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Text(
                            if (usuario.activo == true) "Activo" else "Inactivo",
                            color = if (usuario.activo == true) LightGreen else Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (isDefaultAdmin) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "⚠️ Admin predeterminado - No se puede modificar",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Yellow.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
                if (!isDefaultAdmin) {
                    Row {
                        // Botón para cambiar rol
                        IconButton(
                            onClick = onChangeRole,
                            enabled = !isDefaultAdmin
                        ) { 
                            Icon(
                                Icons.Default.Settings, 
                                "Cambiar Rol", 
                                tint = Color.Cyan
                            ) 
                        }
                        // Botón para eliminar
                        IconButton(
                            onClick = onDelete,
                            enabled = !isDefaultAdmin
                        ) { 
                            Icon(
                                Icons.Default.Delete, 
                                "Eliminar", 
                                tint = Color.Red
                            ) 
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    itemType: String,
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { 
            Text("¿Estás seguro de que quieres eliminar la $itemType \"$itemName\"? Esta acción no se puede deshacer.") 
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AdminDrawerContent(
    selectedContent: String,
    onUsersClicked: () -> Unit,
    onCanchasClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = Grey900) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Menú de Admin",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White
            )
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = contentColor)
    }
}

@Composable
fun ChangeRoleDialog(
    usuario: UsuarioDto,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedRol by remember { 
        mutableStateOf(usuario.rol ?: "USUARIO") 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Rol de Usuario") },
        text = {
            Column {
                Text(
                    "Usuario: ${usuario.nombre} ${usuario.apellido ?: ""}".trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    "Rol actual: ${usuario.rol ?: "USUARIO"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Selecciona el nuevo rol:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón ADMIN
                    FilterChip(
                        selected = selectedRol == "ADMIN",
                        onClick = { selectedRol = "ADMIN" },
                        label = { Text("ADMIN") },
                        modifier = Modifier.weight(1f)
                    )
                    // Botón USUARIO
                    FilterChip(
                        selected = selectedRol == "USUARIO",
                        onClick = { selectedRol = "USUARIO" },
                        label = { Text("USUARIO") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRol) },
                enabled = selectedRol != usuario.rol
            ) {
                Text("Cambiar Rol")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
