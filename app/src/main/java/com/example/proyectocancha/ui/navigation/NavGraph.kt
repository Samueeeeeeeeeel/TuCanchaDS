package com.example.proyectocancha.ui.navigation

import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina


import com.example.proyectocancha.ui.components.AppTopBar// Barra superior
import com.example.proyectocancha.ui.components.AppDrawer// Drawer composable
import com.example.proyectocancha.ui.components.defaultDrawerItems // Ítems por defecto
import com.example.proyectocancha.ui.screen.HomeScreen // Pantalla Home
import com.example.proyectocancha.ui.screen.LoginScreen // Pantalla Login
import com.example.proyectocancha.ui.screen.RegisterScreen // Pantalla Registro


@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController) { // Recibe el controlador

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del drawer
    val scope = rememberCoroutineScope() // Necesario para abrir/cerrar drawer

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Routess.home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Routess.login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Routess.register.path) } // Ir a Registro

    ModalNavigationDrawer( // Capa superior con drawer lateral
        drawerState = drawerState, // Estado del drawer
        drawerContent = { // Contenido del drawer (menú)
            AppDrawer( // Nuestro componente Drawer
                currentRoute = null, // Puedes pasar navController.currentBackStackEntry?.destination?.route
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goLogin() // Navega a Login
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goRegister() // Navega a Registro
                    }
                )
            )
        }
    ) {
        Scaffold( // Estructura base de pantalla
            topBar = { // Barra superior con íconos/menú
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
                    onHome = goHome,     // Botón Home
                    onLogin = goLogin,   // Botón Login
                    onRegister = goRegister // Botón Registro
                )
            }
        ) { innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = Routess.home.path, // Inicio: Home
                modifier = Modifier.padding(innerPadding) // Respeta topBar
            ) {
                composable(Routess.home.path) {
                    HomeScreen(
                        onGoLogin = goLogin,
                        onGoRegister = goRegister
                    )
                }
                composable(Routess.login.path) {
                    LoginScreen(
                        onLoginOk = goHome,
                        onGoRegister = goRegister
                    )
                }
                composable(Routess.register.path) {
                    RegisterScreen(
                        onRegistered = goLogin,
                        onGoLogin = goLogin
                    )

                }
            }
        }
    }
}
