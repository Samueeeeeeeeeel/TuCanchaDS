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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.proyectocancha.ui.domain.validation.validarClaveFuerte
import com.example.proyectocancha.ui.domain.validation.validarEmail


@Composable // Pantalla Login (solo navegación, sin formularios)
fun LoginScreen(
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    //crear un fondo y que centre el contenido
    val bg = MaterialTheme.colorScheme.inverseOnSurface // Fondo distinto para contraste
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center

    ){
        var  email by remember { mutableStateOf("") }
        var  password by remember { mutableStateOf("") }
        var errorEmail by remember { mutableStateOf<String?>(null) }
        var errorPassword by remember { mutableStateOf<String?>(null) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //mostrar el titulo y dejar un espacio para los elementos que van debajo
            Text(
                "Login", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            //Caja de texto para el email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                isError = errorEmail != null,
                modifier = Modifier.fillMaxWidth()
            )

            errorEmail?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            //Caja de texto para la contraseña
            OutlinedTextField(
                value = password, onValueChange = {password = it},
                label = {Text("Contraseña")},
                singleLine = true,
                isError = password != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            errorPassword?.let {
                Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.height(16.dp))

            //Botones de inicnar Sesión y registrarse
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    errorEmail= validarEmail(email)
                    errorPassword= validarClaveFuerte(password)
                    if (errorEmail==null && errorPassword==null){
                        onLoginOkNavigateHome()
                    }

                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50), // Fondo verde
                    contentColor = Color.White // Texto blanco
                )

                ) { Text("Iniciar Sesion")}
            }
        }
    }

}


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginOkNavigateHome = {}, onGoRegister = {})
}