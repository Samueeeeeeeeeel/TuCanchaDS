package com.example.proyectocancha.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyectocancha.ui.screen.AdminScreen
import com.example.proyectocancha.ui.screen.CanchaDetailsScreen
import com.example.proyectocancha.ui.screen.DetalleReservaScreen
import com.example.proyectocancha.ui.screen.LoginScreen
import com.example.proyectocancha.ui.screen.MisReservasScreen
import com.example.proyectocancha.ui.screen.PrincipalScreen
import com.example.proyectocancha.ui.screen.ProfileScreen
import com.example.proyectocancha.ui.screen.ReciboReservaScreen
import com.example.proyectocancha.ui.screen.RegisterScreen
import com.example.proyectocancha.ui.screen.VerCanchasScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routess.login.path
    ) {

        // LOGIN
        composable(Routess.login.path) {
            LoginScreen(
                onLoginOkNavigateHome = { isAdmin ->
                    // si quieres que el admin vaya a otra pantalla, cámbialo aquí
                    navController.navigate(Routess.principal.path) {
                        popUpTo(Routess.login.path) { inclusive = true }
                    }
                },
                onGoRegister = {
                    navController.navigate(Routess.register.path)
                }
            )
        }

        // REGISTRO
        composable(Routess.register.path) {
            RegisterScreen(
                onRegisteredOk = {
                    navController.popBackStack()
                },
                onGoLogin = {
                    navController.popBackStack()
                }
            )
        }

        // HOME PRINCIPAL
        composable(Routess.principal.path) {
            PrincipalScreen(navController = navController)
        }

        // VER TODAS LAS CANCHAS
        composable(Routess.verCanchas.path) {
            VerCanchasScreen(navController = navController)
        }

        // DETALLE DE CANCHA (recibe courtId)
        composable(
            route = "${Routess.courtDetail.path}/{courtId}",
            arguments = listOf(
                navArgument("courtId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courtId = backStackEntry.arguments?.getInt("courtId") ?: 0
            CanchaDetailsScreen(
                navController = navController,
                courtId = courtId
            )
        }

        // DETALLE DE RESERVA (recibe courtId y pricePerHour)
        composable(
            route = "${Routess.detalleReserva.path}/{courtId}/{pricePerHour}",
            arguments = listOf(
                navArgument("courtId") { type = NavType.IntType },
                navArgument("pricePerHour") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courtId = backStackEntry.arguments?.getInt("courtId") ?: 0
            val pricePerHour = backStackEntry.arguments?.getInt("pricePerHour") ?: 0

            DetalleReservaScreen(
                navController = navController,
                courtId = courtId,
                pricePerHour = pricePerHour
            )
        }

        // RECIBO
        composable(Routess.reciboReserva.path) {
            ReciboReservaScreen(navController = navController)
        }

        // MIS RESERVAS
        composable(Routess.misReservas.path) {
            MisReservasScreen(navController = navController)
        }

        // PERFIL
        composable(Routess.profile.path) {
            ProfileScreen(navController = navController)
        }

        // PANEL ADMIN (si no lo usas aún, igual compila)
        composable(Routess.admin.path) {
            AdminScreen(navController = navController)
        }
    }
}
