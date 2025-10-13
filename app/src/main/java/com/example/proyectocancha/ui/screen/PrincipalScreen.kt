package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.R
import com.example.proyectocancha.ui.navigation.Routess
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Promotion
import com.example.proyectocancha.ui.model.dummyCourts
import com.example.proyectocancha.ui.model.dummyPromotions

// --- Definiciones de Tema y Datos (Añade estas a tu archivo de tema o úsalas directamente) ---

// Define colores personalizados (debes tenerlos en tu archivo ui/theme/Color.kt)
val DarkGreen = Color(0xFF4CAF50)
val LightGreen = Color(0xFF8BC34A)

// Datos de ejemplo para las canchas y promociones (usa R.drawable.xxx con tus imágenes)
data class Court(val id: Int, val name: String, val imageUrl: Int, val description: String)
data class Promotion(val id: Int, val title: String, val imageUrl: Int, val ctaText: String)

val dummyPromotions = listOf(
    // Sustituye con tus Drawables. Si tienes errores, es porque no has añadido las imágenes a res/drawable.
    Promotion(1, "¡Promoción Destacada!", R.drawable.court_1, "Reservar"),
    Promotion(2, "Descuento de Verano", R.drawable.court_1, "Ver Oferta")
)

val dummyCourts = listOf(
    Court(1, "Cancha Principal A", R.drawable.court_1, "Cancha con excelentes instalaciones"),
    Court(2, "Cancha B - Sintético", R.drawable.court_1, "Césped sintético de alta calidad"),
    Court(3, "Cancha C - Interior", R.drawable.court_1, "Cancha techada climatizada")
)

// --- Composable PrincipalScreen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TUCANCHA!",
                            color = LightGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar búsqueda */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Routess.profile) }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black // Fondo general de la pantalla
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ver Canchas",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Implementar vista de todas las canchas */ },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Todas las Canchas", color = Color.White)
                }
            }

            item {
                PromotionCard(promotion = dummyPromotions[0]) { promotion ->
                    // Navegar al detalle de la cancha con el ID de la promoción
                    navController.navigate(Routess.courtDetail.path + "/${promotion.id}")
                }
            }

            item {
                Text(
                    text = "Promociones Destacadas",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyCourts.size) { index ->
                        CourtSmallCard(court = dummyCourts[index]) { court ->
                            navController.navigate(Routess.courtDetail.path + "/${court.id}")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Próximas Reservas",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                // Placeholder de Reservas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2C2C2E))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay reservas próximas",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* TODO: Handle View Courts click */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2E)),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Ver Canchas", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { /* TODO: Implementar vista de reservas */ },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Reservar", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- Componentes Reutilizables (Tarjetas) ---

@Composable
fun PromotionCard(promotion: Promotion, onClick: (Promotion) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick(promotion) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = promotion.imageUrl),
                contentDescription = promotion.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart) // Ajustado para parecerse más al diseño
                    .padding(16.dp)
            ) {
                Text(
                    text = promotion.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onClick(promotion) },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text(promotion.ctaText, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CourtSmallCard(court: Court, onClick: (Court) -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp)
            .clickable { onClick(court) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Column {
            Image(
                painter = painterResource(id = court.imageUrl),
                contentDescription = court.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(LightGreen)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = court.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun PrincipalScreenPreview() {
    ProyectoCanchaTheme {
        PrincipalScreen(rememberNavController())
    }
}