package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.ui.NavRoute

@Composable
fun MainBottomAppBar(navController: NavController, active: Int){
    val colors = NavigationBarItemColors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
        selectedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
        disabledIconColor = MaterialTheme.colorScheme.secondary,
        disabledTextColor = MaterialTheme.colorScheme.secondary
    )

    var isNavigatingToAr by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        windowInsets = NavigationBarDefaults.windowInsets,
    ){
        NavigationBarItem(
            label = { Text(stringResource(R.string.books)) },
            colors = colors,
            onClick = { navController.navigate(NavRoute.HomeScreen) },
            selected =  active == 1 ,
            icon = {
                if (active != 1){
                    Icon(Icons.Outlined.Book, "Home")
                }else{
                    Icon(Icons.Filled.Book, "Home")
                }
            }
        )
        NavigationBarItem(
            label = { Text(stringResource(R.string.ar_page)) },
            colors = colors,
            onClick = {
                isNavigatingToAr = true
                navController.navigate(NavRoute.ARScreen)
                      },
            selected = active == 2,
            icon = {
                if (isNavigatingToAr){
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                }else{
                    if (active != 2){
                        Icon(Icons.Outlined.CameraAlt, "Home")
                    }else{
                        Icon(Icons.Filled.CameraAlt, "Home")
                    }
                }

            }
        )
    }
}