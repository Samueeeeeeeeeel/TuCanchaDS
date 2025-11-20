package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.navigation.Routes
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGrayBg
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.CanchaViewModel
import com.example.proyectocancha.ui.viewmodel.CanchaViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanchaDetailsScreen(navController: NavHostController, courtId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val courtRepo = remember { CourtRepository(db.courtDao()) }
    val factory = remember { CanchaViewModelFactory(courtRepo) }
    val vm: CanchaViewModel = viewModel(factory = factory)

    val state by vm.state.collectAsStateWithLifecycle()

    // Llama al ViewModel para que cargue la cancha por su ID
    LaunchedEffect(courtId) {
        vm.loadCourtById(courtId)
    }

    val court = state.selectedCourt
    val clpFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(court?.name ?: "Cargando...", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        bottomBar = {
            if (court != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Grey900).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { 
                            // ¡ERROR CORREGIDO! Se usa Routes en singular
                            navController.navigate(Routes.misReservas.path)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(
                            "Reservar Ahora (${clpFormatter.format(court.price)}/hr)",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        },
        containerColor = Grey900
    ) { paddingValues ->
        if (state.isLoading || court == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightGreen)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(250.dp).background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, "Imagen de cancha", tint = Color.LightGray, modifier = Modifier.size(60.dp))
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(court.name, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Precio por Hora: ${clpFormatter.format(court.price)}", color = DarkGreen, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    Text("Descripción", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(court.description, color = Color.Gray, fontSize = 16.sp)
                    Spacer(Modifier.height(24.dp))

                    Text("Seleccionar Fecha y Hora", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(LightGrayBg).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("(Funcionalidad de reserva deshabilitada temporalmente)", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanchaDetailsScreenPreview() {
    CanchaDetailsScreen(rememberNavController(), courtId = 1)
}
