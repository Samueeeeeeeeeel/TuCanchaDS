package com.example.proyectocancha.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectocancha.R
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.viewmodel.AuthViewModel
import com.example.proyectocancha.ui.viewmodel.AuthViewModelFactory
import com.example.proyectocancha.ui.viewmodel.RemoteAuthViewModel
import com.example.proyectocancha.ui.viewmodel.RemoteAuthViewModelFactory

@Composable
fun LoginScreen(
    onLoginOkNavigateHome: (Boolean) -> Unit,
    onGoRegister: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val repository = remember { UserRepository(db.userDao()) }
    val viewModel: RemoteAuthViewModel = viewModel(factory = RemoteAuthViewModelFactory())

    val state by viewModel.login.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            state.user?.let { user ->
                AuthManager.login(user)
                Log.d("LOGIN_SUCCESS", "Usuario '${user.name}' guardado en AuthManager.")
                onLoginOkNavigateHome(user.isAdmin)
            } ?: run {
                Log.e("LOGIN_ERROR", "Login fue exitoso pero el objeto de usuario es nulo.")
            }
            viewModel.clearLoginResult()
        }
    }

    LoginScreenUi(
        email = state.email,
        password = state.password,
        emailError = state.emailError,
        passwordError = state.passwordError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = viewModel::onLoginEmailChange,
        onPassChange = viewModel::onLoginPassChange,
        onSubmit = viewModel::submitLogin,
        onClearError = viewModel::clearLoginResult,
        onGoRegister = onGoRegister
    )
}

@Composable
private fun LoginScreenUi(
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClearError: () -> Unit,
    onGoRegister: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey900),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(350.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logotucancha),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(180.dp) // Tamaño aumentado
            )
            Spacer(Modifier.height(24.dp))
            // --------------------------

            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico", color = Color.White) },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = emailError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                emailError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPassChange,
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                isError = passwordError != null,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White
                ),
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
            AnimatedVisibility(
                visible = passwordError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                passwordError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...", color = Color.White)
                } else {
                    Text("Iniciar Sesión")
                }
            }

            Spacer(Modifier.height(12.dp))

            ElevatedButton(
                onClick = onGoRegister,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Crear cuenta")
            }
        }

        if (errorMsg != null) {
            AlertDialog(
                onDismissRequest = onClearError,
                title = { Text("Error de Autenticación") },
                text = { Text(errorMsg) },
                confirmButton = {
                    TextButton(onClick = onClearError) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginOkNavigateHome = {}, onGoRegister = {})
}
