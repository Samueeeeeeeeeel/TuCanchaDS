package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.proyectocancha.navigation.Routess
import com.example.proyectocancha.ui.theme.ProyectoCanchaTheme
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.dummyCourts
// ¡NUEVOS IMPORTS!
import com.example.proyectocancha.ui.components.AppTopBar

// ---------------------------------------------------
// ¡NUEVA FUNCIÓN PrincipalScreen (Contenedora)!
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class) // Necesario para Scaffold
@Composable
fun PrincipalScreen(navController: NavController) {

    // Scaffold es la estructura que nos permite poner una TopBar
    Scaffold(
        topBar = {
            // 2. Aquí llamamos a tu AppTopBar
            AppTopBar(
                onOpenDrawer = {
                    // Por ahora, la hamburguesa no hace nada.
                    // Más adelante, aquí irá la lógica para abrir el Drawer.
                },
                onGoLogin = {
                    // Asumimos que tienes una ruta de perfil
                    // Si no la tienes, puedes cambiarla a Routess.login.path
                    navController.navigate(Routess.profile.path)
                }
            )
        }
    ) { innerPadding ->
        // 3. Llamamos a tu contenido original (el LazyColumn)
        //    y le pasamos el 'innerPadding' que nos da el Scaffold.
        PrincipalScreenContent(
            navController = navController,
            paddingValues = innerPadding // <-- ¡CLAVE!
        )
    }
}


// ---------------------------------------------------
// ¡TU FUNCIÓN ORIGINAL, AHORA RENOMBRADA!
// ---------------------------------------------------
@Composable
fun PrincipalScreenContent(navController: NavController, paddingValues: PaddingValues = PaddingValues()) {
    val CardDarkBg = Color(0xFF333333)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey900)
            .padding(paddingValues) // ✅ APLICA EL PADDING DEL SCAFFOLD
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    )

    {
        // 1. Pestañas de acción rápida
        item {
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionsTabRow(navController = navController)
        }

        // 2. Título de sección
        item {
            Text(
                text = "Canchas Destacadas",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 3. Lista horizontal de canchas
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(dummyCourts) { court ->
                    CourtSmallCard(court = court) { selectedCourt ->
                        navController.navigate("${Routess.courtDetail.path}/${selectedCourt.id}")
                    }
                }
            }
        }

        // 4. Mis reservas
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
                        text = "No tienes reservas activas. Toca para ver.",
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

// ---------------------------------------------------
// (El resto de tus funciones auxiliares no cambian)
// ---------------------------------------------------

@Composable
fun QuickActionsTabRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionPill(
            icon = Icons.Default.Menu,
            text = "Mis Reservas",
            onClick = { navController.navigate(Routess.misReservas.path) },
            modifier = Modifier.weight(1f)
        )
        ActionPill(
            icon = Icons.Default.ArrowForward,
            text = "Ver Canchas",
            onClick = { navController.navigate(Routess.verCanchas.path) },
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


// ---------------------------------------------------
// ¡PREVIEW ACTUALIZADO!
// ---------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PrincipalScreenPreview() {
    ProyectoCanchaTheme {
        // Ahora el preview llama a la nueva función contenedora
        PrincipalScreen(navController = rememberNavController())
    }
}