package com.example.proyectocancha.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScrren(
    //Variables
    onGoHome: () -> Unit,
    userName;String,
    userEmail:String,
    userPhone:String,
    onEditProfile:() -> Unit,
    onChangePassword:() -> Unit,
    onLogout:() -> Unit
){
    val bg= MaterialTheme.colorScheme.background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = userName,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

    }
}