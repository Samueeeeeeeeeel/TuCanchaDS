package com.example.proyectocancha.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectocancha.ui.screen.*
import com.example.proyectocancha.ui.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routess.login.path,
        modifier = modifier
    ) {
        // LOGIN
        composable(Routess.login.path) {
            LoginScreen(
                onLoginOkNavigateHome = { _ ->
                    navController.navigate(Routess.principal.path) {
                        popUpTo(Routess.login.path) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Routess.register.path) }
            )
        }

        // REGISTER
        composable(Routess.register.path) {
            RegisterScreen(
                onRegisteredOk = {
                    navController.navigate(Routess.login.path) {
                        popUpTo(Routess.register.path) { inclusive = true }
                    }
                },
                onGoLogin = { navController.popBackStack() }
            )
        }

        // PRINCIPAL
        composable(Routess.principal.path) {
            PrincipalScreen(navController = navController)
        }
        composable(Routess.admin.path) {
            AdminScreen(navController = navController)
        }

        // PROFILE
        composable(Routess.profile.path) {
            ProfileScreen(navController = navController)
        }

        // VER CANCHAS
        composable(Routess.verCanchas.path) {
            VerCanchasScreen(navController = navController)
        }

        // DETALLE CANCHA (argumento courtId)
        composable("${Routess.courtDetail.path}/{courtId}") { backStackEntry ->
            val courtId = backStackEntry.arguments?.getString("courtId")?.toIntOrNull() ?: 0
            CanchaDetailsScreen(navController = navController, courtId = courtId)
        }

        // DETALLE RESERVA
        composable(Routess.detalleReserva.path) {
            DetalleReservaScreen(navController = navController)
        }

        // RECIBO RESERVA
        composable(Routess.reciboReserva.path) {
            ReciboReservaScreen(navController = navController)
        }

        // MIS RESERVAS
        composable(Routess.misReservas.path) {
            MisReservasScreen(navController = navController)
        }
    }
}