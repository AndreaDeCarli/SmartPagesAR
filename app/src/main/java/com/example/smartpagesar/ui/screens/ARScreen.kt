package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartpagesar.ui.composables.MainBottomAppBar

@Composable
fun ARScreen(navController: NavController){
    Scaffold(
        bottomBar = { MainBottomAppBar(navController, 2) }
    ) {
        innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("Ar goes here")
        }
    }
}