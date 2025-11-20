package com.example.proyectocancha.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyectocancha.ui.screen.AdminScreen
import com.example.proyectocancha.ui.screen.CanchaDetailsScreen
import com.example.proyectocancha.ui.screen.LoginScreen
import com.example.proyectocancha.ui.screen.PrincipalScreen
import com.example.proyectocancha.ui.screen.ProfileScreen
import com.example.proyectocancha.ui.screen.ReciboReservaScreen
import com.example.proyectocancha.ui.screen.RegisterScreen
import com.example.proyectocancha.ui.screen.VerCanchasScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.login.path
    ) {

        // LOGIN
        composable(Routes.login.path) {
            LoginScreen(
                onLoginOkNavigateHome = { isAdmin ->
                    // --- ¡LÓGICA CORREGIDA! ---
                    if (isAdmin) {
                        // Si es admin, va al panel de administrador
                        navController.navigate(Routes.admin.path) {
                            popUpTo(Routes.login.path) { inclusive = true }
                        }
                    } else {
                        // Si es usuario normal, va a la pantalla principal
                        navController.navigate(Routes.principal.path) {
                            popUpTo(Routes.login.path) { inclusive = true }
                        }
                    }
                },
                onGoRegister = {
                    navController.navigate(Routes.register.path)
                }
            )
        }

        // REGISTRO
        composable(Routes.register.path) {
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
        composable(Routes.principal.path) {
            PrincipalScreen(navController = navController)
        }

        // VER TODAS LAS CANCHAS
        composable(Routes.verCanchas.path) {
            VerCanchasScreen(navController = navController)
        }

        // DETALLE DE CANCHA (recibe courtId)
        composable(
            route = "${Routes.courtDetail.path}/{courtId}",
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

        // RECIBO
        composable(Routes.reciboReserva.path) {
            ReciboReservaScreen(navController = navController)
        }
        // PERFIL
        composable(Routes.profile.path) {
            ProfileScreen(navController = navController)
        }

        // PANEL ADMIN
        composable(Routes.admin.path) {
            AdminScreen(navController = navController)
        }
    }
}
