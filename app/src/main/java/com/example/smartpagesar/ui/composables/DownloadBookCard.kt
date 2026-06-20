package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.Subject
import com.example.smartpagesar.ui.viewmodels.DownloadBooksViewModel

@Composable
fun DownloadBookCard(
    book: Book,
    viewModel: DownloadBooksViewModel
){
    var showProgress by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0.0f) }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 5.dp, vertical = 6.dp),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.20F),
                contentAlignment = Alignment.Center,
            ){
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
            Column (
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(0.60F)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4F),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.title,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2F),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.author,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.5f)
                    )
                    if (book.subject != null){
                        Text(
                            text = stringResource(Subject.valueOf(book.subject).label),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
                Row(modifier = Modifier.weight(0.5F)) {
                    Column(modifier = Modifier.weight(0.8F), horizontalAlignment = Alignment.Start) {
                        if (showProgress) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("In download: ${(downloadProgress * 100).toInt()}%")
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .height(15.dp),
                                    progress = { downloadProgress },
                                    color = MaterialTheme.colorScheme.tertiary,
                                    strokeCap = StrokeCap.Round,
                                    gapSize = -10.dp
                                )
                            }

                        }else{
                            Spacer(modifier = Modifier.weight(0.8f))
                        }
                    }

                    Box(modifier = Modifier.weight(0.20F)){
                        IconButton(
                            enabled = !showProgress,
                            onClick = {
                                viewModel.downloadBookWithModels(book.id, book.short_id, {
                                        progress -> while (downloadProgress <= progress){
                                    downloadProgress += 0.01f
                                }
                                })
                                showProgress = true
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary),
                        ) {
                            Icon(Icons.Filled.Download, "download")
                        }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun DownloadBookCardPreview(){
//    val book1 = Book("gdag",0,"The Theory of Math", "Tom Cruz", "Math", 12, 2021 )
//    val book2 = Book("", 0,"The history of Us", "Micheal jardan", "History", 12, 2003)
//    val books = arrayOf(book1, book2)
//    val navController = NavController(LocalContext.current)
//    Scaffold(
//        topBar = {MainTopAppBar(navController, stringResource(R.string.books), true)},
//        bottomBar = { MainBottomAppBar(navController, 1)}
//    ) {innerPadding ->
//        LazyColumn(modifier = Modifier
//            .padding(innerPadding)
//            .padding(horizontal = 7.dp)) {
//            items(books){item ->
//                DownloadBookCard(item, {})
//            }
//        }
//
//    }
//}