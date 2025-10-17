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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectocancha.R
import com.example.proyectocancha.ui.navigation.Routess
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.theme.DarkGreen
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.Grey900


// ----------------------------------------------------------------------
// MODELOS Y DATOS (4 Canchas distintas)
// ----------------------------------------------------------------------

data class Court(val id: Int, val name: String, val imageUrl: Int, val description: String)

val dummyCourts = listOf(
    // ESTOS SON LOS DATOS ACTUALIZADOS
    Court(1, "Cancha Norte - Pasto Real", R.drawable.court_1, "Cancha con excelentes instalaciones"),
    Court(2, "Cancha Sur - Sintético", R.drawable.court_1, "Césped sintético de alta calidad"),
    Court(3, "Cancha Valle - Sintético", R.drawable.court_1, "Cancha techada climatizada"),
    Court(4, "Cancha Pick - Baby Fut", R.drawable.court_1, "Cancha con iluminación profesional")
)


// ----------------------------------------------------------------------
// PANTALLA PRINCIPAL
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(navController: NavController) {

    val CardDarkBg = Color(0xFF333333)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TuCancha!",
                            color = LightGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                },
                // CAMBIO CLAVE: Se vacía el bloque actions {}
                actions = {
                    // El espacio para acciones está ahora vacío.
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        },
        containerColor = Grey900
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Grey900)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 1. PESTAÑAS DE ACCIÓN RÁPIDA (Mi Perfil, Mis Reservas, Ver Todas)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                QuickActionsTabRow(navController = navController)
            }

            // 2. TÍTULO DE LA SECCIÓN DE CANCHAS
            item {
                Text(
                    text = "Canchas Destacadas",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. SECCIÓN 'VER CANCHAS' (Lista horizontal de las 4 canchas)
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyCourts.size) { index ->
                        CourtSmallCard(court = dummyCourts[index]) { court ->
                            navController.navigate(Routess.courtDetail.path + "/${court.id}")
                        }
                    }
                }
            }

            // 4. SECCIÓN 'MIS RESERVAS' (Bloque de contenido)
            item {
                Text(
                    text = "Mis Reservas",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
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
                            text = "No tienes reservas activas.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // 5. Espacio final
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ----------------------------------------------------------------------
// COMPONENTES AUXILIARES (Se mantienen igual)
// ----------------------------------------------------------------------

@Composable
fun QuickActionsTabRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Pestaña 1: Mi Perfil
        ActionPill(
            icon = Icons.Filled.AccountCircle,
            text = "Mi Perfil",
            onClick = { navController.navigate(Routess.profile.path) },
            modifier = Modifier.weight(1f)
        )
        // Pestaña 2: Mis Reservas
        ActionPill(
            icon = Icons.Default.Menu,
            text = "Mis Reservas",
            onClick = { /* TODO: Implementar navegación a Mis Reservas */ },
            modifier = Modifier.weight(1f)
        )
        // Pestaña 3: Ver Canchas
        ActionPill(
            icon = Icons.Default.ArrowForward,
            text = "Ver Canchas",
            onClick = { /* TODO: Implementar navegación a lista completa de canchas */ },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionPill(icon: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(80.dp)
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
        colors = CardDefaults.cardColors(containerColor = Grey900)
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

// ----------------------------------------------------------------------
// PREVIEW
// ----------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun PrincipalScreenPreview() {
    ProyectoCanchaTheme {
        PrincipalScreen(rememberNavController())
    }
}