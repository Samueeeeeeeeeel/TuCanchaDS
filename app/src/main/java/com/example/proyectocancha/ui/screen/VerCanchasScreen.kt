package com.example.proyectocancha.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.R // Asegúrate de que R.drawable.court_1 exista
import com.example.proyectocancha.ui.navigation.Routess
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen

// Nota: Asumimos que los modelos Court y dummyCourts son accesibles aquí,
// o se redefinen si están solo en PrincipalScreen. Aquí los redefino para la independencia:
data class Court(val id: Int, val name: String, val imageUrl: Int, val description: String)
val dummyCourts = listOf(
    Court(1, "Cancha Norte - Pasto Real", R.drawable.court_1, "Cancha con excelentes instalaciones"),
    Court(2, "Cancha Sur - Sintético", R.drawable.court_1, "Césped sintético de alta calidad"),
    Court(3, "Cancha Valle - Sintético", R.drawable.court_1, "Cancha techada climatizada"),
    Court(4, "Cancha Pick - Baby Fut", R.drawable.court_1, "Cancha con iluminación profesional"),
    Court(5, "Cancha Central", R.drawable.court_1, "Ideal para torneos."),
    Court(6, "Cancha Río", R.drawable.court_1, "Junto al río con vista panorámica.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerCanchasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las Canchas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        containerColor = Grey900
    ) { paddingValues ->
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
            items(dummyCourts) { court ->
                FullWidthCourtCard(court = court) { selectedCourt ->
                    navController.navigate(Routess.courtDetail.path + "/${selectedCourt.id}")
                }
            }
        }
    }
}

// Tarjeta de cancha de ancho completo
@Composable
fun FullWidthCourtCard(court: Court, onClick: (Court) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(court) }
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column {
            Image(
                painter = painterResource(id = court.imageUrl),
                contentDescription = court.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = court.name,
                    color = LightGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = court.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerCanchasScreenPreview() {
    VerCanchasScreen(rememberNavController())
}