package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.components.AppDrawer
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.defaultDrawerItems
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.viewmodel.CanchaViewModel
import com.example.proyectocancha.ui.viewmodel.CanchaViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val courtRepo = remember { CourtRepository(db.courtDao()) }
    val canchaVm: CanchaViewModel = viewModel(factory = CanchaViewModelFactory(courtRepo))
    val canchaState by canchaVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        canchaVm.loadAllCourts()
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
            if (canchaState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                PrincipalScreenContent(
                    navController = navController,
                    paddingValues = innerPadding,
                    courts = canchaState.courtList
                )
            }
        }
    }
}

@Composable
fun PrincipalScreenContent(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(),
    courts: List<CourtEntity>
) {
    val CardDarkBg = Color(0xFF333333)

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Grey900).padding(paddingValues).padding(horizontal = 16.dp),
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

        items(courts.take(2)) { court ->
            CourtCardPrincipal(court = court) { 
                navController.navigate("${Routes.courtDetail.path}/${court.id}")
            }
        }

        item {
            Text("Mis Reservas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp).clickable { navController.navigate(Routes.misReservas.path) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Toca para ver tus reservas", color = Color.Gray, fontSize = 16.sp)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun CourtCardPrincipal(court: CourtEntity, onClick: (CourtEntity) -> Unit) { 
    Card(
        modifier = Modifier.fillMaxWidth().height(230.dp).clickable { onClick(court) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                if (court.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = File(court.imageUrl)),
                        contentDescription = court.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = "Sin imagen", tint = Color.LightGray)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = court.name, color = LightGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = court.description, color = Color.Gray, fontSize = 14.sp, maxLines = 2)
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
