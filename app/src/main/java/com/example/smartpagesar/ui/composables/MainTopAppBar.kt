package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navController: NavController, title: String,  goBack: Boolean, action: @Composable (() -> Unit) = {}, onMenuOpen: () -> Unit = {}){
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (goBack) {
                IconButton( onClick = {
                    //additionalAction()
                    navController.navigateUp()
                }) {
                    Icon(Icons.Filled.ArrowBackIosNew, "Back")
                }
            }else {
                IconButton(
                    onClick = onMenuOpen
                ) {
                    Icon(Icons.Filled.Menu, "Menu")
                }
            }
         },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = { Row(){
            action()
        } }

    )
}