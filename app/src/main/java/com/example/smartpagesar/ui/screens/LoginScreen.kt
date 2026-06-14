package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { MainTopAppBar(navController, "Login", true, {}) }
    ) {
        innerPadding -> Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.login({ onLoginSuccess(); isLoading = false })},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.signin))
            if (isLoading){
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    strokeWidth = 2.dp)
            }else{
                Icon(Icons.Outlined.Login, "login")
            }
        }

        if (uiState.error != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(300.dp))

        Button(
            onClick = { navController.navigate(NavRoute.RegisterScreen) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrati")
        }
    }
    }

}
