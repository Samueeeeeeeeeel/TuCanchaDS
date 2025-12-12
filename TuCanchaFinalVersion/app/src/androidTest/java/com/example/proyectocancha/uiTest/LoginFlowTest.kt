package com.example.proyectocancha.uiTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.proyectocancha.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de instrumentación para el flujo de Login y Registro.
 * Este test se ejecuta en un emulador o dispositivo real.
 */
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {

    // Lanza la MainActivity antes de cada test
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginExitoso_comoAdmin_navegaAPantallaAdmin() {
        // 1. Escribir en el campo de email
        composeTestRule.onNodeWithText("Correo Electrónico").performTextInput("admin@duoc.cl")

        // 2. Escribir en el campo de contraseña
        composeTestRule.onNodeWithText("Contraseña").performTextInput("Admin123!")

        // 3. Hacer clic en el botón de Iniciar Sesión
        composeTestRule.onNode(hasText("Iniciar Sesión") and hasClickAction()).performClick()

        // 4. Esperar y verificar que la pantalla de Admin es visible
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNode(hasText("Panel de Administrador") and hasAnyAncestor(isRoot())).isDisplayed()
        }

        // 5. Afirmar que el texto está realmente en pantalla
        composeTestRule.onNodeWithText("Panel de Administrador").assertIsDisplayed()
    }

    @Test
    fun registroExitoso_navegaAPantallaLogin() {
        // 1. Ir a la pantalla de registro
        composeTestRule.onNodeWithText("Crear cuenta").performClick()

        // 2. Rellenar el formulario de registro
        val uniqueEmail = "newuser_${System.currentTimeMillis()}@test.com"
        composeTestRule.onNodeWithText("Nombre Completo").performTextInput("Usuario de Prueba")
        composeTestRule.onNodeWithText("Teléfono").performTextInput("987654321")
        composeTestRule.onNodeWithText("Correo Electrónico").performTextInput(uniqueEmail)
        composeTestRule.onNodeWithText("Contraseña").performTextInput("Password123!")
        composeTestRule.onNodeWithText("Confirmar Contraseña").performTextInput("Password123!")

        // 3. Hacer clic en el botón de registrarse
        composeTestRule.onNode(hasText("CREAR CUENTA") and hasClickAction()).performClick()

        // 4. Esperar y verificar que volvemos a la pantalla de Login
        // Buscamos un elemento único de la pantalla de Login para evitar ambigüedad
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNode(hasText("Crear cuenta")).isDisplayed()
        }

        // 5. Afirmar que el botón para crear cuenta está visible
        composeTestRule.onNodeWithText("Crear cuenta").assertIsDisplayed()
    }
}