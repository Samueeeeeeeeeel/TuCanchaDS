package com.example.proyectocancha.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job

// IMPORTACIONES CORRECTAS DE COMPONENTES Y PANTALLAS
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.AppDrawer
import com.example.proyectocancha.ui.components.defaultDrawerItems
import com.example.proyectocancha.ui.screen.PrincipalScreen
import com.example.proyectocancha.ui.screen.LoginScreen
import com.example.proyectocancha.ui.screen.RegisterScreen
import com.example.proyectocancha.ui.screen.CanchaDetailsScreen
import com.example.proyectocancha.ui.screen.ProfileScreen
import com.example.proyectocancha.ui.screen.DetalleReservaScreen // <-- ¡IMPORTACIÓN AÑADIDA!


// *******************************************************************
// NOTA IMPORTANTE: Asumo que Routess.detalleReserva está en minúsculas.
// *******************************************************************

@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- Helpers de Navegación ---
    val goPrincipal: () -> Unit = { navController.navigate(Routess.principal.path) }
    val goLogin: () -> Unit   = { navController.navigate(Routess.login.path) }
    val goRegister: () -> Unit = { navController.navigate(Routess.register.path) }
    val goProfile: () -> Unit = { navController.navigate(Routess.profile.path) }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goPrincipal()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    },
                    onProfile = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routess.profile.path)
                    }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goPrincipal,
                    onProfile = goProfile
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routess.login.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // 1. PANTALLA DE LOGIN
                composable(Routess.login.path) {
                    LoginScreen(
                        onLoginOkNavigateHome = {
                            navController.navigate(Routess.principal.path) {
                                popUpTo(Routess.login.path) { inclusive = true }
                            }
                        },
                        onGoRegister = goRegister
                    )
                }

                // 2. PANTALLA PRINCIPAL
                composable(Routess.principal.path) {
                    PrincipalScreen(navController = navController)
                }

                // 3. DETALLE DE CANCHA
                composable(
                    route = Routess.courtDetail.path + "/{courtId}",
                    arguments = listOf(navArgument("courtId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val courtId = backStackEntry.arguments?.getInt("courtId") ?: 0
                    CanchaDetailsScreen(navController = navController, courtId = courtId)
                }

                // 4. PERFIL DE USUARIO
                composable(Routess.profile.path) {
                    ProfileScreen(navController = navController)
                }

                // 5. REGISTRO
                composable(Routess.register.path) {
                    RegisterScreen(
                        onRegisteredOk = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // 6. RUTA 'HOME'
                composable(Routess.home.path) {
                    PrincipalScreen(navController = navController)
                }

                // 7. DETALLE DE RESERVA
                composable(Routess.detalleReserva.path) {
                    DetalleReservaScreen(navController = navController)
                }
            }
        }
    }
}
// ¡La función DetalleReservaScreen con el TODO fue eliminada de aquí!