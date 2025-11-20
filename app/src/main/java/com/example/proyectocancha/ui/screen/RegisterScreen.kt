package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.viewmodel.AuthViewModel
import com.example.proyectocancha.ui.viewmodel.AuthViewModelFactory
import com.example.proyectocancha.ui.viewmodel.RegisterUistate
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.data.local.database.AppDatabase

@Composable
fun RegisterScreen(
    onRegisteredOk: () -> Unit,
    onGoLogin: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val repository = remember { UserRepository(db.userDao()) }
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    val state: RegisterUistate by viewModel.register.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            onRegisteredOk()
            viewModel.clearRegisterResult()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey900)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                "Crear una Cuenta",//
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nombre Completo", color = Color.White) },
                singleLine = true,
                isError = state.nameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            state.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Teléfono", color = Color.White) },
                singleLine = true,
                isError = state.phoneError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            state.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onRegisterEmailChange(it) },
                label = { Text("Correo Electrónico", color = Color.White) },
                singleLine = true,
                isError = state.emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onRegisterPassChange(it) },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                isError = state.passError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            state.passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.confirm,
                onValueChange = { viewModel.onConfirmChange(it) },
                label = { Text("Confirmar Contraseña", color = Color.White) },
                singleLine = true,
                isError = state.confirmError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            state.confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.submitRegister() },
                enabled = state.canSubmit && !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(if (state.isSubmitting) "Creando..." else "CREAR CUENTA")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ya tengo una cuenta, iniciar sesión", color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            state.errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisteredOk = {}, onGoLogin = {})
}