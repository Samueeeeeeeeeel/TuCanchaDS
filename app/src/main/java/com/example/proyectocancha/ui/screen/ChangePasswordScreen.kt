package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectocancha.data.local.database.AppDatabase
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.ui.theme.Grey900
import com.example.proyectocancha.ui.theme.LightGreen
import com.example.proyectocancha.ui.viewmodel.ChangePasswordViewModel
import com.example.proyectocancha.ui.viewmodel.ChangePasswordViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val viewModel: ChangePasswordViewModel = viewModel(
        factory = ChangePasswordViewModelFactory(userRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.navigateUp()
        }
    }

    Scaffold(
        containerColor = Grey900,
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Grey900)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LightGreen,
                unfocusedBorderColor = Color.Gray,
                cursorColor = LightGreen,
                errorBorderColor = Color.Red,
                focusedLabelColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
                errorLabelColor = Color.Red
            )

            // Contraseña Actual
            OutlinedTextField(
                value = uiState.currentPassword,
                onValueChange = { viewModel.onCurrentPasswordChange(it) },
                label = { Text("Contraseña actual") },
                textStyle = TextStyle(color = Color.White), // <-- FORZAMOS EL COLOR DEL TEXTO
                visualTransformation = if (uiState.isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = uiState.currentPasswordError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleCurrentPasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isCurrentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (uiState.isCurrentPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            if (uiState.currentPasswordError != null) {
                Text(uiState.currentPasswordError!!, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }

            // Nueva Contraseña
            OutlinedTextField(
                value = uiState.newPassword,
                onValueChange = { viewModel.onNewPasswordChange(it) },
                label = { Text("Nueva contraseña") },
                textStyle = TextStyle(color = Color.White), // <-- FORZAMOS EL COLOR DEL TEXTO
                visualTransformation = if (uiState.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = uiState.newPasswordError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleNewPasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (uiState.isNewPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            if (uiState.newPasswordError != null) {
                Text(uiState.newPasswordError!!, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }

            // Confirmar Nueva Contraseña
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar nueva contraseña") },
                textStyle = TextStyle(color = Color.White), // <-- FORZAMOS EL COLOR DEL TEXTO
                visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = uiState.confirmPasswordError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                trailingIcon = {
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (uiState.isConfirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            if (uiState.confirmPasswordError != null) {
                Text(uiState.confirmPasswordError!!, color = Color.Red, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = { viewModel.changePassword() },
                enabled = uiState.isSubmittable,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Cambiar Contraseña")
                }
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = Color.Red)
            }
        }
    }
}
