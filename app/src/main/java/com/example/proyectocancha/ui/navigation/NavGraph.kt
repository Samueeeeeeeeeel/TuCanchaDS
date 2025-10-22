package com.example.proyectocancha.ui.navigation

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
import androidx.navigation.compose.currentBackStackEntryAsState

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

@Composable
fun AppNavGraph(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- Helpers de Navegación ---
    val goPrincipal: () -> Unit = { navController.navigate(Routess.principal.path) }
    val goLogin: () -> Unit = { navController.navigate(Routess.login.path) }
    val goRegister: () -> Unit = { navController.navigate(Routess.register.path) }
    val goProfile: () -> Unit = { navController.navigate(Routess.profile.path) }

    //ruta para el administrador
    val goAdmin:()-> Unit = {navController.navigate(Routess.adminPrincipal.path) }

    // --- ¡CAMBIOS AQUÍ! ---
    // Añadimos los helpers para las nuevas rutas del drawer
    val goVerCanchas: () -> Unit = { navController.navigate(Routess.verCanchas.path) } // <-- AÑADIDO
    val goMisReservas: () -> Unit = { navController.navigate(Routess.misReservas.path) } // <-- AÑADIDO
    // --- FIN DE CAMBIOS ---


    // --- Lógica para mostrar/ocultar la barra (Esto está perfecto) ---
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBarraYDrawer = when {
        currentRoute == null -> false
        currentRoute.startsWith(Routess.login.path) -> false
        currentRoute.startsWith(Routess.register.path) -> false
        else -> true
    }
    // --- FIN DE LA LÓGICA ---


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBarraYDrawer, // Perfecto
        drawerContent = {
            AppDrawer(
                currentRoute = null,

                // --- ¡CAMBIOS AQUÍ! ---
                // Actualizamos la llamada a defaultDrawerItems para que coincida
                // con la nueva firma de la función en AppDrawer.kt
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goPrincipal()
                    },
                    onProfile = {
                        scope.launch { drawerState.close() }
                        goProfile() // Usamos el helper
                    },
                    onVerCanchas = { // <-- AÑADIDO
                        scope.launch { drawerState.close() }
                        goVerCanchas()
                    },
                    onMisReservas = { // <-- AÑADIDO
                        scope.launch { drawerState.close() }
                        goMisReservas()
                    }
                )
                // --- FIN DE CAMBIOS ---
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showBarraYDrawer) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onGoLogin = goProfile // (Esto estaba bien de la vez anterior)
                    )
                }
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
                        onLoginOkNavigateHome = { userIsAdmin ->
                            if (userIsAdmin) {
                                //si es admin navega a la pantalla de admin
                                navController.navigate(Routess.adminPrincipal.path) {
                                    popUpTo(Routess.login.path) { inclusive = true }
                                }
                            } else {
                                // si no tiene rol de admin navegar al home
                                navController.navigate(Routess.principal.path) {
                                    popUpTo(Routess.login.path) { inclusive = true }
                                }
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
                // 11. PANTALLA PRINCIPAL ADMIN
                composable(Routess.adminPrincipal.path) {
                    AdminScreen(navController = navController)
                }
            }
        }
    }
}