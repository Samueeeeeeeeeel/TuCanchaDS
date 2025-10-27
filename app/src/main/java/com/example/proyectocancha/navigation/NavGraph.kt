package com.example.proyectocancha.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination // <-- ¡NUEVO! Importar

// IMPORTACIONES CORRECTAS DE COMPONENTES Y PANTALLAS
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.AppDrawer
import com.example.proyectocancha.ui.components.defaultDrawerItems
import com.example.proyectocancha.ui.screen.PrincipalScreen
import com.example.proyectocancha.ui.screen.LoginScreen
import com.example.proyectocancha.ui.screen.RegisterScreen
import com.example.proyectocancha.ui.screen.CanchaDetailsScreen
import com.example.proyectocancha.ui.screen.ProfileScreen
import com.example.proyectocancha.ui.screen.DetalleReservaScreen
import com.example.proyectocancha.ui.screen.MisReservasScreen
import com.example.proyectocancha.ui.screen.ReciboReservaScreen
import com.example.proyectocancha.ui.screen.VerCanchasScreen
import com.example.proyectocancha.ui.screen.AdminScreen // IMPORTACIÓN DE LA PANTALLA DE ADMIN
import com.example.proyectocancha.ui.viewmodel.CourtDetailViewModel
import com.example.proyectocancha.data.repository.CourtRepository
@Composable
fun AppNavGraph(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



    // --- Helpers de Navegación ---
    val goPrincipal: () -> Unit = { navController.navigate(Routess.principal.path) }
    val goLogin: () -> Unit = { navController.navigate(Routess.login.path) }
    val goRegister: () -> Unit = { navController.navigate(Routess.register.path) }
    val goProfile: () -> Unit = { navController.navigate(Routess.profile.path) }
    val goVerCanchas: () -> Unit = { navController.navigate(Routess.verCanchas.path) }
    val goMisReservas: () -> Unit = { navController.navigate(Routess.misReservas.path) }

    // <-- ¡NUEVO! Acción de Logout
    val goLogout: () -> Unit = {
        navController.navigate(Routess.login.path) {
            // Limpia toda la pila de navegación (backstack)
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            // Asegura que la pantalla de Login sea una única instancia
            launchSingleTop = true
        }
    }


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Lógica para mostrar/ocultar barra y drawer
    val showBarraYDrawer = when {
        currentRoute == null -> false
        currentRoute.startsWith(Routess.login.path) -> false
        currentRoute.startsWith(Routess.register.path) -> false
        currentRoute.startsWith(Routess.courtDetail.path) -> false
        currentRoute.startsWith(Routess.adminPrincipal.path) -> false // Ocultar barra en AdminScreen
        else -> true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBarraYDrawer,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                // <-- ¡MODIFICADO! Se añade onLogout
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goPrincipal()
                    },
                    onProfile = {
                        scope.launch { drawerState.close() }
                        goProfile()
                    },
                    onVerCanchas = {
                        scope.launch { drawerState.close() }
                        goVerCanchas()
                    },
                    onMisReservas = {
                        scope.launch { drawerState.close() }
                        goMisReservas()
                    },
                    // <-- ¡NUEVO! Pasar la acción de logout
                    onLogout = {
                        scope.launch { drawerState.close() }
                        goLogout()
                    }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showBarraYDrawer) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        // CORRECCIÓN: El parámetro es onLoginClick
                        onGoLogin = goProfile // <--- ¡SOLUCIÓN!
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routess.login.path,
                // Aplicar el padding al NavHost
                modifier = Modifier.padding(innerPadding)
            ) {

                // 1. PANTALLA DE LOGIN
                composable(Routess.login.path) {
                    LoginScreen(
                        onLoginOkNavigateHome = { userIsAdmin ->
                            if (userIsAdmin) {
                                // Redirección a la pantalla de administrador
                                navController.navigate(Routess.adminPrincipal.path) {
                                    popUpTo(Routess.login.path) { inclusive = true }
                                }
                            } else {
                                // Redirección a la pantalla principal de usuario
                                navController.navigate(Routess.principal.path) {
                                    popUpTo(Routess.login.path) { inclusive = true }
                                }
                            }
                        },
                        onGoRegister = goRegister
                    )
                }

                // 2. PANTALLA PRINCIPAL (Requiere paddingValues)
                composable(Routess.principal.path) {
                    // CORRECCIÓN: Pasar el paddingValues
                    PrincipalScreen(navController = navController, paddingValues = innerPadding)
                }

                // 3. DETALLE DE CANCHA (No requiere padding, ya tiene su propio Scaffold)
                /// 3. DETALLE DE CANCHA (CORREGIDO)
                composable(
                    // ✅ CORRECCIÓN: Agregamos el argumento a la ruta
                    route = Routess.courtDetail.path + "/{courtId}",
                    arguments = listOf(navArgument("courtId") { type = NavType.IntType })
                ) { backStackEntry ->

                    // ✅ Ahora, courtId tiene el valor correcto
                    val courtId = backStackEntry.arguments?.getInt("courtId") ?: 0

                    // 🚨 Problema de la Factory (ver sección 2)
                    val viewModel = viewModel<CourtDetailViewModel>()

                    CanchaDetailsScreen(
                        navController = navController,
                        courtId = courtId,
                        viewModel = viewModel
                    )
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

                // 6. RUTA 'HOME' (Requiere paddingValues)
                composable(Routess.home.path) {
                    // CORRECCIÓN: Pasar el paddingValues
                    PrincipalScreen(navController = navController, paddingValues = innerPadding)
                }

                // 7. DETALLE DE RESERVA
                composable(Routess.detalleReserva.path) {
                    DetalleReservaScreen(navController = navController)
                }

                // 8. RECIBO DE RESERVA
                composable(Routess.reciboReserva.path) {
                    ReciboReservaScreen(navController = navController)
                }

                // 9. MIS RESERVAS
                composable(Routess.misReservas.path) {
                    MisReservasScreen(navController = navController)
                }

                // 10. VER CANCHAS
                composable(Routess.verCanchas.path) {
                    VerCanchasScreen(navController = navController)
                }

                // 11. PANTALLA DE ADMINISTRADOR
                composable(Routess.adminPrincipal.path) {
                    AdminScreen(navController = navController)
                }
            }
        }
    }
}