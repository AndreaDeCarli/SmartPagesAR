package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.composables.MainTopAppBar

@Composable
fun HomeScreen(navController: NavController, loginButtonAction: ()-> Unit){
    Scaffold(
        bottomBar = { MainBottomAppBar(navController, 1) },
        topBar = { MainTopAppBar(navController, "Home", false, {
            IconButton(
                onClick = loginButtonAction )
            { Icon(Icons.Filled.AccountCircle, "")  }
        }) }
    ) {innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("deca")
        }
    }
}