// ProfileScreen.kt
package com.example.proyectocancha.ui.screen

// --- NUEVO: Imports para la cámara y permisos ---
import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.ui.viewmodel.ProfileViewModel
import com.example.proyectocancha.ui.viewmodel.ProfileViewModelFactory

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

    // --- VIEWMODEL PARA OBTENER DATOS REALES DEL USUARIO ---
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userRepository)
    )
    val uiState by profileViewModel.state.collectAsStateWithLifecycle()
    // --------------------------------------------------------


    // --- ESTADOS PARA FOTO / CÁMARA / GALERÍA ---
    var showImagePicker by remember { mutableStateOf(false) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { nuevoBitmap -> bitmap = nuevoBitmap }
    )

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
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

    val permisoCamara = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val permisoGaleria = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    // Acción de logout (solo navega y limpia el backstack)
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

        // DIÁLOGO PARA ELEGIR CÁMARA O GALERÍA
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
                                if (permisoGaleria.status.isGranted) {
                                    launcherGaleria.launch("image/*")
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ---------- TARJETA CON DATOS DEL USUARIO ----------
            item {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else {
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

                            // Foto de perfil
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

                            // Información real del usuario
                            if (uiState.email.isBlank()) {
                                Text(
                                    text = "No hay un usuario autenticado",
                                    color = textColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            } else {
                                Text(
                                    text = "Información Personal",
                                    color = textColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = uiState.nombre,
                                    color = textColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Correo: ${uiState.email}",
                                    color = mutedTextColor,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Teléfono: ${uiState.telefono}",
                                    color = mutedTextColor,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón cambiar foto
                            TextButton(onClick = { showImagePicker = true }) {
                                Text("Cambiar Foto", color = accentColor)
                            }
                        }
                    }
                }
            }

            // ---------- BOTÓN CERRAR SESIÓN ----------
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

// Placeholder para SettingItem (si más adelante lo necesitas)
@Composable
private fun SettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
        Spacer(Modifier.width(16.dp))
        Text(text = label, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProyectoCanchaTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
