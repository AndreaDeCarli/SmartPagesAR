package com.example.smartpagesar.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.InteractiveModel
import com.example.smartpagesar.data.models.Subject
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
            title = stringResource(R.string.details_title),
            goBack = true,
            ) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .shadow(6.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Row(Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Text(book.title, fontSize = 24.sp)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.padding(10.dp)) {
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
                                        .background(MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(10.dp))
                                        .fillMaxSize()
                                )
                            } else{
                                AsyncImage(
                                    model = imageUrl,
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "Book Cover",
                                    modifier = Modifier
                                        .shadow(3.dp, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .fillMaxSize()
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.60f)
                                .padding(bottom = 12.dp, start = 15.dp)
                                .fillMaxHeight()
                        ){
                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){Text("${stringResource(R.string.generic_author)}: ${book.author}", fontSize = 18.sp)}

                            if (book.subject != null){
                                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("${
                                    stringResource(
                                        R.string.generic_subject
                                    )
                                }: ${stringResource(Subject.valueOf(book.subject.uppercase()).label)}", fontSize = 18.sp) }
                            }

                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("${
                                stringResource(
                                    R.string.generic_year
                                )
                            }: ${book.year}", fontSize = 18.sp) }

                            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)){ Text("${
                                stringResource(
                                    R.string.generic_chapters
                                )
                            }: ${book.chapters}", fontSize = 18.sp) }

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
