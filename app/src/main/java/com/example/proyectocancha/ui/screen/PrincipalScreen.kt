package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.BookingRepository
import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.components.AppDrawer
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.defaultDrawerItems
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.dummyCourts
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.viewmodel.BookingViewModel
import com.example.proyectocancha.ui.viewmodel.BookingViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController) {

    // Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // BookingViewModel para contar reservas activas
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val bookingRepo = remember { BookingRepository(db.bookingDao()) }
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(bookingRepo)
    )
    val bookingState by bookingViewModel.state.collectAsStateWithLifecycle()
    val activeCount = bookingState.activeCount

    val drawerItems = defaultDrawerItems(
        onHome = {
            scope.launch { drawerState.close() }
        },
        onProfile = {
            navController.navigate(Routess.profile.path)
            scope.launch { drawerState.close() }
        },
        onVerCanchas = {
            navController.navigate(Routess.verCanchas.path)
            scope.launch { drawerState.close() }
        },
        onMisReservas = {
            navController.navigate(Routess.misReservas.path)
            scope.launch { drawerState.close() }
        },
        onLogout = {
            navController.navigate(Routess.login.path) {
                popUpTo(Routess.principal.path) { inclusive = true }
            }
        }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = drawerItems
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onGoLogin = {
                        navController.navigate(Routess.profile.path)
                    }
                )
            }
        ) { innerPadding ->
            PrincipalScreenContent(
                navController = navController,
                paddingValues = innerPadding,
                activeBookings = activeCount
            )
        }
    }
}

@Composable
fun PrincipalScreenContent(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(),
    activeBookings: Int
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
        // --- TÍTULOS ---
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Explora y reserva tu cancha favorita",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recomendadas",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- SOLO DOS CANCHAS: NORTE y SUR ---
        items(dummyCourts.take(2)) { court ->
            CourtCard(court = court) {
                navController.navigate("${Routess.courtDetail.path}/${court.id}")
            }
        }

        // --- MIS RESERVAS (LO MISMO QUE TENÍAS) ---
        item {
            Text(
                text = "Mis Reservas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            val textReservas = when {
                activeBookings <= 0 -> "No tienes reservas activas. Toca para ver."
                activeBookings == 1 -> "Tienes 1 reserva activa. Toca para ver."
                else -> "Tienes $activeBookings reservas activas. Toca para ver."
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { navController.navigate(Routess.misReservas.path) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textReservas,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CourtCard(court: Court, onClick: (Court) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable { onClick(court) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column {
            Image(
                painter = painterResource(id = court.imageUrl),
                contentDescription = court.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = court.name,
                    color = LightGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = court.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrincipalScreenPreview() {
    ProyectoCanchaTheme {
        PrincipalScreen(navController = rememberNavController())
    }
}
