package com.example.smartpagesar.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.DownloadBookCard
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.DownloadBooksViewModel

@Composable
fun DownloadBooksScreen(
    navController: NavController,
    viewModel: DownloadBooksViewModel,
    books: List<Book>,
    onLoadImage: (String) -> String
    ){
        var isDownloading by remember { mutableStateOf(false) }
        BackHandler(enabled = true) { }

        Scaffold(
        topBar = {MainTopAppBar(navController, stringResource(R.string.download_books), false)},
        floatingActionButton = { FloatingActionButton(
            onClick = { navController.navigate(NavRoute.HomeScreen) }
        ){
            Icon(Icons.Default.Check, "check")
        } }
    ) {innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 7.dp)) {
            items(books){item ->
                var downloadProgress by remember { mutableFloatStateOf(0.0f) }
                DownloadBookCard(item, viewModel, onLoadImage)
            }
        }

    }
}