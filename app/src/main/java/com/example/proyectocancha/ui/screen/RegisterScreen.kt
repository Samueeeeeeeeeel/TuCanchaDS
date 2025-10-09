package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
// NOTA: Se eliminó la importación de KeyboardOptions y KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Importar las funciones de validación de tu dominio
import com.example.proyectocancha.ui.domain.validation.validarEmail
import com.example.proyectocancha.ui.domain.validation.validarClaveFuerte
import com.example.proyectocancha.ui.domain.validation.validarNombreSoloLetras
import com.example.proyectocancha.ui.domain.validation.validatePhoneisDigitsOnly
import com.example.proyectocancha.ui.domain.validation.validarConfirmacion


@Composable
fun RegisterScreen(
    onRegisteredOk: () -> Unit,
    onGoLogin: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.inverseOnSurface
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. MANEJO DEL ESTADO DE LOS CAMPOS
        var nombre by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        // 2. MANEJO DE ERRORES DE VALIDACIÓN
        var errorNombre by remember { mutableStateOf<String?>(null) }
        var errorTelefono by remember { mutableStateOf<String?>(null) }
        var errorEmail by remember { mutableStateOf<String?>(null) }
        var errorPassword by remember { mutableStateOf<String?>(null) }
        var errorConfirmPassword by remember { mutableStateOf<String?>(null) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Permite hacer scroll si el contenido es muy largo
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                "Crear una Cuenta",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(32.dp))

            // CAMPO: Nombre (Validación de solo letras)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = null },
                label = { Text("Nombre Completo") },
                singleLine = true,
                isError = errorNombre != null,
                modifier = Modifier.fillMaxWidth()
            )
            errorNombre?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPO: Teléfono (Validación de solo dígitos)
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it; errorTelefono = null },
                label = { Text("Teléfono") },
                singleLine = true,
                isError = errorTelefono != null,
                // --- ELIMINADO: keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            errorTelefono?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPO: Email (Validación de formato)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorEmail = null },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                isError = errorEmail != null,
                // --- ELIMINADO: keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            errorEmail?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPO: Contraseña (Validación de fuerza)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorPassword = null; errorConfirmPassword = null },
                label = { Text("Contraseña") },
                singleLine = true,
                isError = errorPassword != null,
                visualTransformation = PasswordVisualTransformation(),
                // --- ELIMINADO: keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            errorPassword?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // CAMPO: Confirmar Contraseña (Validación de igualdad)
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorConfirmPassword = null },
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                isError = errorConfirmPassword != null,
                visualTransformation = PasswordVisualTransformation(),
                // --- ELIMINADO: keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            errorConfirmPassword?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(32.dp))

            // BOTÓN PRINCIPAL: REGISTRARSE
            Button(
                onClick = {
                    // Ejecutar TODAS las validaciones
                    errorNombre = validarNombreSoloLetras(nombre)
                    errorTelefono = validatePhoneisDigitsOnly(telefono)
                    errorEmail = validarEmail(email)
                    errorPassword = validarClaveFuerte(password)
                    errorConfirmPassword = validarConfirmacion(password, confirmPassword)

                    // Si todas las validaciones son nulas (correctas), navega
                    if (errorNombre == null && errorTelefono == null && errorEmail == null && errorPassword == null && errorConfirmPassword == null) {
                        onRegisteredOk()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2), // Azul fuerte
                    contentColor = Color.White
                )
            ) {
                Text("CREAR CUENTA")
            }

            Spacer(Modifier.height(12.dp))

            // BOTÓN SECUNDARIO: IR A LOGIN
            OutlinedButton(
                onClick = onGoLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ya tengo una cuenta, iniciar sesión")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisteredOk = {}, onGoLogin = {})
}