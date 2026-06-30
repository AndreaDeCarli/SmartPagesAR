package com.example.smartpagesar.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.toRoute
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
import com.example.smartpagesar.ui.screens.BookDetailScreen
import com.example.smartpagesar.ui.screens.DownloadBooksScreen
import com.example.smartpagesar.ui.screens.SettingsScreen
import com.example.smartpagesar.ui.screens.StorageScreen
import com.example.smartpagesar.ui.viewmodels.ARScreenViewModel
import com.example.smartpagesar.ui.viewmodels.ARScreenViewModelFactory
import com.example.smartpagesar.ui.viewmodels.BookDetailViewModel
import com.example.smartpagesar.ui.viewmodels.BookDetailViewModelFactory
import com.example.smartpagesar.ui.viewmodels.BooksViewModel
import com.example.smartpagesar.ui.viewmodels.BooksViewModelFactory
import com.example.smartpagesar.ui.viewmodels.DownloadBooksViewModel
import com.example.smartpagesar.ui.viewmodels.DownloadBooksViewModelFactory
import com.example.smartpagesar.ui.viewmodels.ProfileViewModel
import com.example.smartpagesar.ui.viewmodels.ProfileViewModelFactory
import com.example.smartpagesar.ui.viewmodels.SettingsState
import com.example.smartpagesar.ui.viewmodels.SettingsViewModel
import com.example.smartpagesar.ui.viewmodels.StorageViewModel
import com.example.smartpagesar.ui.viewmodels.StorageViewModelFactory
import io.github.jan.supabase.storage.storage

sealed interface NavRoute{
    @Serializable data object HomeScreen : NavRoute
    @Serializable data object ARScreen : NavRoute
    @Serializable data object LoginScreen: NavRoute
    @Serializable data object ProfileScreen: NavRoute
    @Serializable data object RegisterScreen: NavRoute
    @Serializable data object SettingsScreen: NavRoute
    @Serializable data object DownloadBooksScreen: NavRoute
    @Serializable data class BookDetailScreen(val bookId: String): NavRoute
    @Serializable data object StorageScreen: NavRoute
}

@Composable
fun SmartPagesARNavGraph(navController: NavHostController, settingsState: SettingsState, settingsViewModel: SettingsViewModel){
    val ctx = LocalContext.current
    val app = ctx.applicationContext as SmartPagesARApplication

    fun navigateIfLoggedIn(route: NavRoute, otherRoute: NavRoute){
        if (app.supabase.auth.currentSessionOrNull() !== null) {
            navController.navigate(otherRoute)
        }else{
            navController.navigate(route)
        }
    }


    NavHost(navController = navController,
        startDestination = NavRoute.HomeScreen
    ){
        composable<NavRoute.HomeScreen> {

            val vm: BooksViewModel = viewModel(
                factory = BooksViewModelFactory(app.supabase)
            )
            val books by vm.books.collectAsState()
            val isLoading by vm.isLoading.collectAsState()

            LaunchedEffect(Unit) {
                vm.loadDownloadedBooks()
            }

            HomeScreen(
                navController,
                books,
                loginButtonAction = { navigateIfLoggedIn(NavRoute.LoginScreen, NavRoute.ProfileScreen) },
                floatingActionButtonAction = { navigateIfLoggedIn(NavRoute.HomeScreen,NavRoute.DownloadBooksScreen) },
                isUserLoggedIn = app.supabase.auth.currentSessionOrNull() !== null,
                onDelete = {book -> vm.deleteDownloadedBook(book, ctx)},
                onLoadImage = { path -> app.supabase.storage["BookCovers"].publicUrl(path) },
                isLoading = isLoading
                )
        }

        composable<NavRoute.ARScreen> {

            val arViewModel: ARScreenViewModel = viewModel(
                factory = ARScreenViewModelFactory(LocalContext.current.applicationContext as Application)
            )

            ARScreen(navController, arViewModel, settingsState)
        }
        composable<NavRoute.LoginScreen> {
            val loginVm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(app.supabase)
            )

            LoginScreen(
                viewModel = loginVm,
                onLoginSuccess = { navController.navigate(NavRoute.HomeScreen) },
                navController = navController
            )
        }

        composable<NavRoute.RegisterScreen> {
            val registerVm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(app.supabase)
            )

            RegisterScreen(navController, registerVm) { navController.navigate(NavRoute.HomeScreen) }
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
        composable<NavRoute.DownloadBooksScreen> {

            val vm: DownloadBooksViewModel = viewModel(
                factory = DownloadBooksViewModelFactory(app.supabase, ctx)
            )
            val booksToDownload by vm.filteredBooks.collectAsState()

            DownloadBooksScreen(
                navController,
                vm,
                booksToDownload,
                { path -> app.supabase.storage["BookCovers"].publicUrl(path) })
        }
        composable<NavRoute.BookDetailScreen> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.BookDetailScreen>()

            val vm: BookDetailViewModel = viewModel(
                factory = BookDetailViewModelFactory(app.supabase, route.bookId)
            )

            val models by vm.models.collectAsState()
            val book by vm.book.collectAsState()

            val imageUrl = book.image?.let { app.supabase.storage["BookCovers"].publicUrl(it) } ?: ""

            BookDetailScreen(navController,book, models, ctx, imageUrl)

        }
        composable<NavRoute.StorageScreen> {

            val vm: StorageViewModel = viewModel(
                factory = StorageViewModelFactory(ctx)
            )

            val folders by vm.folders.collectAsState()
            val totalSize by vm.totalSize.collectAsState()

            StorageScreen(navController, folders, totalSize)
        }
    }
}