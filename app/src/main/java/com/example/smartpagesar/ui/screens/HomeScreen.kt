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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
fun HomeScreen(navController: NavController, loginButtonAction: ()-> Unit){

    // TODO remove these things for testing
    val book1 = Book("gdag","The Theory of Math", "Tom Cruz", "Math", 12, 2021 )
    val book2 = Book("", "The history of Us", "Micheal jardan", "History", 12, 2003)
    val books = arrayOf(book1, book2)


    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    onClick = { navController.navigate(NavRoute.SettingsScreen) },
                    icon = { Icon(Icons.Filled.Settings, "settings") }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = { MainBottomAppBar(navController, 1) },
            topBar = { MainTopAppBar(
                navController,
                stringResource(R.string.books),
                false,
                {IconButton(onClick = loginButtonAction ){ Icon(Icons.Filled.AccountCircle, "")  }},
                { scope.launch{ drawerState.open() } }
                ) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {  }
                ) { Icon(Icons.Default.Download, "download") }
            }
        ) {innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding).padding(horizontal = 7.dp)) {
                items(books){item ->
                    BookCard(item)
                }
            }
        }
    }
}