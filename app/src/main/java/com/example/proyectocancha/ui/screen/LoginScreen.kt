package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.ui.viewmodel.AuthViewModel // Importación de tu VM

// ----------------------------------------------------------------------
// 1. FUNCIÓN CONTENEDORA (Conectada al ViewModel)
// ----------------------------------------------------------------------

@Composable
fun LoginScreen(
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    // 1. Obtiene/Crea el ViewModel
    val vm: AuthViewModel = viewModel()

    // 2. Observa el Estado del Login (StateFlow)
    val state by vm.login.collectAsStateWithLifecycle()

    // 3. Lógica de Navegación tras Éxito
    if (state.success) {
        vm.clearLoginResult() // Limpia banderas (success = false, errorMsg = null)
        onLoginOkNavigateHome() // Navega
    }

    // 4. Delega a la UI presentacional
    LoginScreenUi(
        email = state.email,
        password = state.password,
        emailError = state.emailError,
        passwordError = state.passwordError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg, // ERROR UNIFICADO DE CREDENCIALES
        onEmailChange = vm::onLoginEmailChange,
        onPassChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister
    )
}

// ----------------------------------------------------------------------
// 2. UI PRESENTACIONAL (Recibe todos los datos como parámetros)
// ----------------------------------------------------------------------

@Composable
private fun LoginScreenUi(
    email: String,
    password: String,
    emailError: String?, // Se mantiene para el contrato, pero se ignora en la UI
    passwordError: String?, // Se mantiene para el contrato, pero se ignora en la UI
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?, // <-- ESTA ES LA VARIABLE CLAVE PARA EL MENSAJE UNIFICADO
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.inverseOnSurface
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),// Fondo negro
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LOGIN", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(Modifier.height(16.dp))

            // ---------- EMAIL ----------
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                singleLine = true,
                // isError se mantiene: si el VM lo envía, se marca, aunque no mostremos el texto de error.
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            emailError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall // Estilo pequeño para errores
                )
            }


            Spacer(Modifier.height(16.dp))

            // ---------- CONTRASEÑA ----------
            OutlinedTextField(
                value = password,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passwordError != null,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            // Eliminamos el bloque 'passwordError?.let { ... }'

            Spacer(Modifier.height(16.dp))

            // ---------- ERROR GLOBAL DE CREDENCIALES UNIFICADO (CORRECCIÓN) ----------
            // Este bloque debe ser el único que muestre el error de credenciales.
            errorMsg?.let {
                Text(
                    text = it, // Muestra el mensaje "EMAIL O CONTRASEÑAS INVÁLIDOS"
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // ---------- BOTONES ----------
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Iniciar Sesión")
                }

            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
            // Eliminamos el último 'errorMsg?.let' duplicado que estaba aquí.

        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    // Usamos la versión UI para el Preview para no depender del ViewModel
    LoginScreenUi(
        email = "usuario@ejemplo.com", password = "password123",
        emailError = null, passwordError = null,
        canSubmit = true, isSubmitting = false, errorMsg = null,
        onEmailChange = {}, onPassChange = {}, onSubmit = {}, onGoRegister = {}
    )
}