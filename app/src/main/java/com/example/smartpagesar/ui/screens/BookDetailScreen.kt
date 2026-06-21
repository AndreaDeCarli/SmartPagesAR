package com.example.smartpagesar.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import coil3.compose.AsyncImage
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import com.example.smartpagesar.ui.NavRoute
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.composables.ModelCard

@Composable
fun BookDetailScreen(
    navController: NavController,
    book: Book,
    models: List<InteractiveModel>,
    context: Context,
    imageUrl: String
){
    Scaffold(
        topBar = { MainTopAppBar(
            navController = navController,
            title = "Book details",
            goBack = true,
            ) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Row(Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Text(book.title, fontSize = 24.sp)
                    }
                    Row(modifier = Modifier.padding(10.dp)) {
                        Column(
                            modifier = Modifier
                                .weight(0.60f)
                                .padding(bottom = 12.dp)
                                .fillMaxHeight()
                        ){
                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){Text("Author: ${book.author}", fontSize = 18.sp)}

                            if (book.subject != null){
                                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("Subject: ${book.subject}", fontSize = 18.sp) }
                            }

                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("Year: ${book.year}", fontSize = 18.sp) }

                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("Chapters: ${book.chapters}", fontSize = 18.sp) }

                        }
                        Box(
                            modifier = Modifier.weight(0.40f)
                        ) {
                            if(imageUrl == ""){
                                Image(
                                    Icons.Outlined.Image,
                                    contentDescription = "Placeholder picture",
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .fillMaxSize()
                                )
                            } else{
                                AsyncImage(
                                    model = imageUrl,
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "Book Cover",
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier
                .padding(10.dp)
                .padding(horizontal = 7.dp)) {
                items(models) { item ->
                    ModelCard(item,book.short_id, context)
                }
            }
        }
    }
}
