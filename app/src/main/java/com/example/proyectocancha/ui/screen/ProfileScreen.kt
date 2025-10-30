package com.example.proyectocancha.ui.screen

// --- NUEVO: Imports para la cámara y permisos ---
import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder // --- NUEVO ---
import android.net.Uri // --- NUEVO ---
import android.os.Build // --- NUEVO ---
import android.provider.MediaStore // --- NUEVO ---
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
// --- FIN NUEVO ---

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext // --- NUEVO ---
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val mainBg = Grey900
    val cardBg = Color(0xFF333333)
    val textColor = Color.White
    val mutedTextColor = Color.Gray
    val errorColor = Color(0xFFF44336)
    val accentColor = LightGreen

    // --- NUEVO: Contexto y estado para el diálogo ---
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    // --- FIN NUEVO ---

    // --- ESTADO PARA LA IMAGEN ---
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    // --- LAUNCHER CÁMARA ---
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { nuevoBitmap ->
            bitmap = nuevoBitmap
        }
    )

    // --- NUEVO: LAUNCHER GALERÍA ---
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Convertimos la Uri a Bitmap para que sea compatible con el estado
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        }
    )

    // --- PERMISO CÁMARA ---
    val permisoCamara = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    // --- NUEVO: PERMISO GALERÍA ---
    // Pide el permiso correcto según la versión de Android
    val permisoGaleria = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    // Datos ficticios
    val userName = "Juan Pérez"
    val userEmail = "juan.perez@example.com"
    val userPhone = "+56 9 8765 4321"

    // Acciones
    val onLogout: () -> Unit = {
        navController.navigate(Routess.login.path) {
            popUpTo(Routess.principal.path) { inclusive = true }
        }
    }


    Scaffold(
        containerColor = mainBg,
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBg,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        }
    ) { innerPadding ->

        // --- NUEVO: Diálogo de selección ---
        if (showImagePicker) {
            AlertDialog(
                onDismissRequest = { showImagePicker = false },
                title = { Text("Cambiar Foto de Perfil") },
                text = {
                    Column {
                        Text("¿Cómo te gustaría agregar tu foto?")
                        Spacer(Modifier.height(16.dp))
                        TextButton(
                            onClick = {
                                // 1. Lógica de Cámara
                                if (permisoCamara.status.isGranted) {
                                    launcherCamara.launch()
                                } else {
                                    permisoCamara.launchPermissionRequest()
                                }
                                showImagePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tomar Foto")
                        }
                        TextButton(
                            onClick = {
                                // 2. Lógica de Galería
                                if (permisoGaleria.status.isGranted) {
                                    launcherGaleria.launch("image/*") // Lanza el selector de galería
                                } else {
                                    permisoGaleria.launchPermissionRequest()
                                }
                                showImagePicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Elegir de la Galería")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImagePicker = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        // --- FIN Diálogo ---

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Información personal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Contenedor de la foto de perfil
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Grey900)
                        ) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Foto de perfil",
                                    tint = mutedTextColor,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // ... (Resto de los Text con info de usuario)
                        Text(
                            text = "Información Personal",
                            color = textColor,
                            // ...
                        )
                        Text(
                            text = userName,
                            color = textColor,
                            // ...
                        )
                        // ...
                        Text(
                            "Correo: $userEmail",
                            // ...
                        )
                        // ...
                        Text(
                            "Teléfono: $userPhone",
                            // ...
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- CAMBIO: Botón para cambiar la foto ---
                        // Ahora abre el diálogo en lugar de la cámara directamente
                        TextButton(onClick = {
                            showImagePicker = true
                        }) {
                            Text("Cambiar Foto", color = accentColor)
                        }
                        // --- FIN CAMBIO ---
                    }
                }
            }

            // Botón cerrar sesión
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor),
                    border = BorderStroke(1.dp, errorColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar sesión", fontSize = 16.sp)
                }
            }
        }
    }
}


// (Tu función SettingItem y Preview se mantienen igual)
@Composable
private fun SettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    // ...
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProyectoCanchaTheme {
        ProfileScreen(navController = rememberNavController())
    }
}