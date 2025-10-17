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
import kotlinx.coroutines.Job // Necesario para el tipo de retorno en AppTopBar

// IMPORTACIONES CORRECTAS DE COMPONENTES Y PANTALLAS
import com.example.proyectocancha.ui.components.AppTopBar
import com.example.proyectocancha.ui.components.AppDrawer // Importación del componente Drawer
import com.example.proyectocancha.ui.components.defaultDrawerItems // Importación de los ítems del menú
import com.example.proyectocancha.ui.screen.PrincipalScreen
import com.example.proyectocancha.ui.screen.LoginScreen
import com.example.proyectocancha.ui.screen.RegisterScreen
import com.example.proyectocancha.ui.screen.CanchaDetailsScreen
import com.example.proyectocancha.ui.screen.ProfileScreen


// *******************************************************************
// NOTA IMPORTANTE: Asumo que Routess está definido en Routess.kt
// y que el tipo 'defaultDrawerItems' en AppDrawer se corrigió a List<DrawerItem>
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
                    onLogin = goLogin,
                    onRegister = goRegister,
                    onProfile = goProfile
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                // CAMBIO 1: Establecer la ruta del Login como el destino inicial (startDestination).
                startDestination = Routess.login.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // 1. PANTALLA DE LOGIN (Ahora es el inicio)
                composable(Routess.login.path) {
                    LoginScreen(
                        // Lógica para ir a Principal después de un login exitoso.
                        onLoginOkNavigateHome = {
                            navController.navigate(Routess.principal.path) {
                                // Esto borra el Login de la pila para evitar volver con el botón 'atrás'.
                                popUpTo(Routess.login.path) { inclusive = true }
                            }
                        },
                        onGoRegister = goRegister
                    )
                }

                // 2. PANTALLA PRINCIPAL (Ahora es la Home App después del login)
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
                // NOTA: Tienes esta ruta duplicada (aquí y abajo), la mantengo para el cambio.
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

                // 6. RUTA 'HOME' (Redirección si se usa)
                composable(Routess.home.path) {
                    PrincipalScreen(navController = navController)
                }
            }
        }
    }
}