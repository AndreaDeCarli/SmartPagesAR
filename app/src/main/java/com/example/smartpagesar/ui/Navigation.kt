package com.example.smartpagesar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.smartpagesar.SmartPagesARApplication
import com.example.smartpagesar.ui.screens.ARScreen
import com.example.smartpagesar.ui.screens.HomeScreen
import com.example.smartpagesar.ui.screens.LoginScreen
import com.example.smartpagesar.ui.screens.ProfileScreen
import com.example.smartpagesar.ui.screens.RegisterScreen
import com.example.smartpagesar.ui.viewmodels.LoginViewModel
import com.example.smartpagesar.ui.viewmodels.LoginViewModelFactory
import com.example.smartpagesar.ui.viewmodels.RegisterViewModel
import com.example.smartpagesar.ui.viewmodels.RegisterViewModelFactory
import io.github.jan.supabase.gotrue.auth
import kotlinx.serialization.Serializable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.smartpagesar.data.models.User
import com.example.smartpagesar.ui.screens.SettingsScreen
import com.example.smartpagesar.ui.viewmodels.ProfileViewModel
import com.example.smartpagesar.ui.viewmodels.ProfileViewModelFactory
import com.example.smartpagesar.ui.viewmodels.SettingsState
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

sealed interface NavRoute{
    @Serializable data object HomeScreen : NavRoute
    @Serializable data object ARScreen : NavRoute
    @Serializable data object LoginScreen: NavRoute
    @Serializable data object  ProfileScreen: NavRoute
    @Serializable data object  RegisterScreen: NavRoute
    @Serializable data object  SettingsScreen: NavRoute
}

@Composable
fun SmartPagesARNavGraph(navController: NavHostController, settingsState: SettingsState, settingsViewModel: SettingsViewModel){
    val ctx = LocalContext.current
    val app = ctx.applicationContext as SmartPagesARApplication
    val scope = rememberCoroutineScope()

    NavHost(navController = navController,
        startDestination = NavRoute.HomeScreen
    ){
        composable<NavRoute.HomeScreen> {
            HomeScreen(navController) {
                if (app.supabase.auth.currentSessionOrNull() === null) {
                    navController.navigate(NavRoute.LoginScreen)
                }else{
                    navController.navigate(NavRoute.ProfileScreen)
                }
            }
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
                onLoginSuccess = { navController.navigate(NavRoute.HomeScreen) },
                navController = navController
            )
        }
        composable<NavRoute.ProfileScreen> {  }
        composable<NavRoute.RegisterScreen> {
            val registerVm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(app.supabase)
            )

            RegisterScreen(navController, registerVm) { navController.navigate(NavRoute.LoginScreen) }
        }
        composable<NavRoute.ProfileScreen> {

            val loginVm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(app.supabase)
            )

            val vm: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(app.supabase)
            )

            ProfileScreen(navController, user = vm.user) { loginVm.logout( { navController.navigate(NavRoute.HomeScreen)} ) }
        }
        composable<NavRoute.SettingsScreen> {
            SettingsScreen(
                navController = navController,
                state = settingsState,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
