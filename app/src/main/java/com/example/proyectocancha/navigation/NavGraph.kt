package com.example.proyectocancha.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectocancha.ui.screen.*
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.ui.viewmodel.AuthViewModel
import com.example.proyectocancha.ui.viewmodel.AuthViewModelFactory

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val repository = remember { UserRepository(db.userDao()) }
    val authFactory = remember { AuthViewModelFactory(repository) }
    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

    NavHost(navController = navController, startDestination = Routess.login.path) {
        composable(Routess.login.path) {
            LoginScreen(
                onLoginOkNavigateHome = { isAdmin ->
                    val route = if (isAdmin) Routess.admin.path else Routess.principal.path
                    navController.navigate(route) {
                        popUpTo(Routess.login.path) { inclusive = true }
                    }
                },
                onGoRegister = { navController.navigate(Routess.register.path) }
            )
        }

        composable(Routess.register.path) {
            RegisterScreen(
                onRegisteredOk = { navController.navigate(Routess.login.path) },
                onGoLogin = { navController.popBackStack() }
            )
        }

        composable(Routess.principal.path) {
            PrincipalScreen(navController = navController)
        }

        composable(Routess.admin.path) {
            AdminScreen(navController = navController) // <-- AHORA SÍ
        }

        composable(Routess.profile.path) {
            ProfileScreen(navController = navController)
        }

        composable(Routess.verCanchas.path) {
            VerCanchasScreen(navController = navController)
        }

        composable("${Routess.courtDetail.path}/{courtId}") { backStackEntry ->
            val courtId = backStackEntry.arguments?.getString("courtId")?.toIntOrNull() ?: 0
            CanchaDetailsScreen(navController = navController, courtId = courtId)
        }

        composable(Routess.detalleReserva.path) {
            DetalleReservaScreen(navController = navController)
        }

        composable(Routess.reciboReserva.path) {
            ReciboReservaScreen(navController = navController)
        }

        composable(Routess.misReservas.path) {
            MisReservasScreen(navController = navController)
        }
    }
}
