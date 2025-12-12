package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.proyectocancha.data.remote.dto.CanchaDto
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerCanchasScreen(navController: NavHostController) {
    // Usar RemoteCanchaViewModel para obtener canchas del BACKEND
    val vm: RemoteCanchaViewModel = viewModel(factory = RemoteCanchaViewModelFactory())
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.loadCanchasActivas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las Canchas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
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
                            text = state.errorMsg ?: "Error desconocido",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { vm.loadCanchasActivas() },
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            state.canchasList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay canchas disponibles",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Selecciona tu cancha ideal",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }
                    items(state.canchasList) { cancha ->
                        CanchaCard(cancha = cancha) { selectedCancha ->
                            navController.navigate("${Routes.courtDetail.path}/${selectedCancha.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CanchaCard(cancha: CanchaDto, onClick: (CanchaDto) -> Unit) {
    val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(cancha) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la cancha o icono por defecto
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = LightGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!cancha.imagenUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = cancha.imagenUrl,
                        contentDescription = cancha.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
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
                                imageVector = getIconForTipo(cancha.tipo),
                                contentDescription = cancha.tipo,
                                tint = LightGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = getIconForTipo(cancha.tipo),
                        contentDescription = cancha.tipo,
                        tint = LightGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cancha.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cancha.tipo,
                    color = LightGreen,
                    fontSize = 14.sp
                )
                Text(
                    text = cancha.direccion,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${clpFormatter.format(cancha.precioPorHora)}/hora",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Obtener icono según tipo de cancha
 */
fun getIconForTipo(tipo: String): ImageVector {
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
fun VerCanchasScreenPreview() {
    VerCanchasScreen(rememberNavController())
}
