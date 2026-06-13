package com.example.smartpagesar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.smartpagesar.SmartPagesARApplication
import com.example.smartpagesar.ui.screens.ARScreen
import com.example.smartpagesar.ui.screens.HomeScreen
import com.example.smartpagesar.ui.screens.LoginScreen
import com.example.smartpagesar.ui.viewmodels.LoginViewModel
import com.example.smartpagesar.ui.viewmodels.LoginViewModelFactory
import kotlinx.serialization.Serializable

sealed interface NavRoute{
    @Serializable data object HomeScreen : NavRoute
    @Serializable data object ARScreen : NavRoute
    @Serializable data object LoginScreen: NavRoute
}

@Composable
fun SmartPagesARNavGraph(navController: NavHostController){ //TODO add settings viewmodel
    val ctx = LocalContext.current
    val app = ctx.applicationContext as SmartPagesARApplication


    NavHost(navController = navController,
        startDestination = NavRoute.HomeScreen
    ){
        composable<NavRoute.HomeScreen> {
            HomeScreen(navController)
        }

        composable<NavRoute.ARScreen> {
            ARScreen(navController)
        }
        composable<NavRoute.LoginScreen> {
            val loginVm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(app.supabase)
            )

            val uiState by loginVm.uiState.collectAsStateWithLifecycle()

            LoginScreen(
                viewModel = loginVm,
                onLoginSuccess = { navController.navigate(NavRoute.HomeScreen) }
            )
        }
    }
}
