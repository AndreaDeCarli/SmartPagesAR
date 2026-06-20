package com.example.smartpagesar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.MainTopAppBar

@Composable
fun BookDetailScreen(
    navController: NavController,
    book: Book,
    models: List<InteractiveModel>
){
    Scaffold(
        topBar = { MainTopAppBar(
            navController = navController,
            title = book.title,
            goBack = true,
            ) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) { }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.primary)

                ,
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(book.title)
                }
                Row() {
                    Column(
                        modifier = Modifier.weight(0.75f)
                    ){
                        Row(Modifier.fillMaxWidth()) {
                            Text(book.author, fontSize = 12.sp)
                        }
                        if (book.subject != null){
                            Row(Modifier.fillMaxWidth()) {
                                Text(book.subject, fontSize = 12.sp)
                            }
                        }

                    }
                    Box(
                        modifier = Modifier.weight(0.25f)
                    ) {
                        Image(
                            Icons.Outlined.Image,
                            contentDescription = "Placeholder picture",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .fillMaxSize()
                        )

                    }
                }

            }
            LazyColumn(modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 7.dp)) {
                items(models) { item ->

                }
            }
        }
    }
}
