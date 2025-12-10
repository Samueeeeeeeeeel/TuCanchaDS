package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanchaDetailsScreen(navController: NavHostController, courtId: Int) {
    // Usar RemoteCanchaViewModel para obtener datos del BACKEND
    val vm: RemoteCanchaViewModel = viewModel(factory = RemoteCanchaViewModelFactory())
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(courtId) {
        vm.loadCanchaById(courtId.toLong())
    }

    val cancha = state.selectedCancha
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Grey900)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            navController.navigate("${Routes.detalleReserva.path}/${cancha.id}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            "Reservar Ahora (${clpFormatter.format(cancha.precioPorHora)}/hr)",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LightGreen)
                }
            }
            state.errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.errorMsg ?: "Error",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { vm.loadCanchaById(courtId.toLong()) },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            cancha == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancha no encontrada", color = Color.Gray)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header con imagen de la cancha o icono por defecto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(LightGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!cancha.imagenUrl.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = cancha.imagenUrl,
                                contentDescription = cancha.nombre,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = LightGreen,
                                        strokeWidth = 3.dp
                                    )
                                },
                                error = {
                                    Icon(
                                        imageVector = getDetailIconForTipo(cancha.tipo),
                                        contentDescription = cancha.tipo,
                                        tint = LightGreen,
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                            )
                        } else {
                            Icon(
                                imageVector = getDetailIconForTipo(cancha.tipo),
                                contentDescription = cancha.tipo,
                                tint = LightGreen,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Nombre
                        Text(
                            text = cancha.nombre,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Tipo de cancha
                        Surface(
                            color = LightGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = cancha.tipo,
                                color = LightGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Precio
                        Text(
                            text = "Precio por Hora",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = clpFormatter.format(cancha.precioPorHora),
                            color = DarkGreen,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Ubicación
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = LightGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ubicación",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cancha.direccion,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        cancha.ciudad?.let { ciudad ->
                            Text(
                                text = ciudad,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Descripción
                        cancha.descripcion?.let { descripcion ->
                            Text(
                                text = "Descripción",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = descripcion,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Toca \"Reservar Ahora\" para elegir la fecha y la hora.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Obtener icono según tipo de cancha para detalles
 */
fun getDetailIconForTipo(tipo: String): ImageVector {
    return when (tipo.lowercase()) {
        "fútbol", "futbol" -> Icons.Default.SportsSoccer
        "básquet", "basquet", "basketball" -> Icons.Default.SportsBasketball
        "tenis" -> Icons.Default.SportsTennis
        "vóley", "voley", "volleyball" -> Icons.Default.SportsVolleyball
        else -> Icons.Default.SportsSoccer
    }
}

@Preview(showBackground = true)
@Composable
fun CanchaDetailsScreenPreview() {
    CanchaDetailsScreen(rememberNavController(), courtId = 1)
}
