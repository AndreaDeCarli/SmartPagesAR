package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.BookCard
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.composables.MainTopAppBar
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    books: List<Book>,
    loginButtonAction: ()-> Unit,
    floatingActionButtonAction: () -> Unit,
    isUserLoggedIn: Boolean,
    onDelete: (Book)->Unit
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet{
                Row( modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {scope.launch { drawerState.close() }}) { Icon(Icons.Default.ArrowBackIosNew, "back") }
                    Text(stringResource(R.string.drawer_title))
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.settings_title)) },
                    selected = false,
                    onClick = {
                        navController.navigate(NavRoute.SettingsScreen)
                        scope.launch { drawerState.close() }
                              },
                    icon = { Icon(Icons.Filled.Settings, "settings") }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = { MainBottomAppBar(navController, 1) },
            topBar = { MainTopAppBar(
                navController = navController,
                title = stringResource(R.string.books),
                goBack = false,
                menu = true,
                action = {IconButton(onClick = loginButtonAction ){ Icon(Icons.Filled.AccountCircle, "")  }},
                onMenuOpen = { scope.launch{ drawerState.open() } }
                ) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isUserLoggedIn){
                            floatingActionButtonAction()
                        }else{
                            scope.launch {
                                snackbarHostState.showSnackbar("Can't download books if not logged in")
                            }
                        }
                    }
                ) { Icon(Icons.Default.Download, "download") }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {innerPadding ->

            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 7.dp)) {
                if (!books.isEmpty()){
                    items(books){item ->
                        BookCard(item, { onDelete(item) }, { id -> navController.navigate( NavRoute.BookDetailScreen(id)) })
                    }
                }else{
                    item { Text(stringResource(R.string.no_books)) }
                }
            }
        }
    }
}