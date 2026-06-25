package com.example.smartpagesar.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material3.Text
import com.example.smartpagesar.R
import com.example.smartpagesar.ui.composables.MainTopAppBar
import com.example.smartpagesar.ui.viewmodels.BookFolder

@SuppressLint("DefaultLocale")
@Composable
fun StorageScreen(
    navController: NavController,
    folders: List<BookFolder>,
    totalSize: Long
){
    Scaffold(
        topBar = { MainTopAppBar(
            navController = navController,
            title = stringResource(R.string.storage_title),
            goBack = true
        ) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(5.dp, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth() ,horizontalArrangement = Arrangement.Center) {
                        LinearProgressIndicator(
                            modifier = Modifier.shadow(5.dp).height(25.dp).fillMaxWidth(),
                            progress = { totalSize.toFloat() / 1024 / 1024 / 1024 },
                            color = MaterialTheme.colorScheme.tertiary,
                            strokeCap = StrokeCap.Butt,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                    Row(modifier = Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.Start) {
                        Text(stringResource(R.string.storage_used) + ": ${String.format("%.2f", totalSize.toFloat() /1024/1024) }MB/1GB", fontSize = 18.sp)
                    }
                }
            }
            items(folders) { folder ->
                Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Row { Text(folder.name, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                    folder.models.forEach {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(
                                Modifier.weight(0.10f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Icon(Icons.Default.SubdirectoryArrowRight, "subdirectory")
                            }
                            Column(
                                Modifier.weight(0.60f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(it.name, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column(
                                Modifier.weight(0.30f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("${String.format("%.2f", it.fileSize)}MB", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp))
            }
        }
    }
}