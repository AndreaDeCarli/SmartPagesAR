package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.ui.composables.DownloadBookCard
import com.example.smartpagesar.ui.composables.MainBottomAppBar
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.DownloadBooksViewModel

@Composable
fun DownloadBooksScreen(
    navController: NavController,
    viewModel: DownloadBooksViewModel,
    books: List<Book>
    ){

    Scaffold(
        topBar = {MainTopAppBar(navController, stringResource(R.string.books), true)},
        bottomBar = { MainBottomAppBar(navController, 1)}
    ) {innerPadding ->
        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 7.dp)) {
            items(books){item ->
                DownloadBookCard(item, { viewModel.markBookAsDownloaded(item.id) })
            }
        }

    }
}