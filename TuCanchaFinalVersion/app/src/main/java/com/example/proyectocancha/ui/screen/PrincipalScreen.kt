package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.data.remote.dto.CanchaDto
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.components.AppDrawer
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.defaultDrawerItems
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteCanchaViewModelFactory
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController) {
    // Usar RemoteCanchaViewModel para obtener canchas del BACKEND
    val canchaVm: RemoteCanchaViewModel = viewModel(factory = RemoteCanchaViewModelFactory())
    val canchaState by canchaVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        canchaVm.loadCanchasActivas()
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerItems = defaultDrawerItems(
        onHome = { scope.launch { drawerState.close() } },
        onProfile = { navController.navigate(Routes.profile.path); scope.launch { drawerState.close() } },
        onVerCanchas = { navController.navigate(Routes.verCanchas.path); scope.launch { drawerState.close() } },
        onMisReservas = { navController.navigate(Routes.misReservas.path); scope.launch { drawerState.close() } },
        onLogout = { navController.navigate(Routes.login.path) { popUpTo(Routes.principal.path) { inclusive = true } } }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(currentRoute = Routes.principal.path, items = drawerItems) }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onGoProfile = { navController.navigate(Routes.profile.path) }
                )
            }
        ) { innerPadding ->
            when {
                canchaState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LightGreen)
                    }
                }
                canchaState.errorMsg != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Grey900)
                            .padding(innerPadding),
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
                                onClick = { canchaVm.loadCanchasActivas() },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    PrincipalScreenContent(
                        navController = navController,
                        paddingValues = innerPadding,
                        canchas = canchaState.canchasList
                    )
                }
            }
        }
    }
}

@Composable
fun PrincipalScreenContent(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(),
    canchas: List<CanchaDto>
) {
    val CardDarkBg = Color(0xFF333333)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey900)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Bienvenido", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Explora y reserva tu cancha favorita", color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recomendadas", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // Mostrar las primeras 3 canchas como recomendadas
        items(canchas.take(3)) { cancha ->
            CanchaCardPrincipal(cancha = cancha) {
                navController.navigate("${Routes.courtDetail.path}/${cancha.id}")
            }
        }

        item {
            // Botón para ver todas las canchas
            if (canchas.size > 3) {
                OutlinedButton(
                    onClick = { navController.navigate(Routes.verCanchas.path) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LightGreen)
                ) {
                    Text("Ver todas las canchas (${canchas.size})")
                }
            }
        }

        item {
            Text(
                "Mis Reservas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { navController.navigate(Routes.misReservas.path) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Toca para ver tus reservas", color = Color.Gray, fontSize = 16.sp)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun CanchaCardPrincipal(cancha: CanchaDto, onClick: (CanchaDto) -> Unit) {
    val clpFormatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(cancha) },
        shape = RoundedCornerShape(16.dp),
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
                    .size(80.dp)
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
                                modifier = Modifier.size(24.dp),
                                color = LightGreen,
                                strokeWidth = 2.dp
                            )
                        },
                        error = {
                            Icon(
                                imageVector = getPrincipalIconForTipo(cancha.tipo),
                                contentDescription = cancha.tipo,
                                tint = LightGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = getPrincipalIconForTipo(cancha.tipo),
                        contentDescription = cancha.tipo,
                        tint = LightGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cancha.nombre,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cancha.tipo,
                    color = LightGreen,
                    fontSize = 14.sp
                )
                cancha.descripcion?.let { desc ->
                    Text(
                        text = desc,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${clpFormatter.format(cancha.precioPorHora)}/hora",
                    color = LightGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Obtener icono según tipo de cancha
 */
fun getPrincipalIconForTipo(tipo: String): ImageVector {
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
fun PrincipalScreenPreview() {
    ProyectoCanchaTheme {
        PrincipalScreen(navController = rememberNavController())
    }
}
