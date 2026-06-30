package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import coil3.compose.AsyncImage
import com.example.smartpagesar.R
import com.example.smartpagesar.data.models.Book
import com.example.smartpagesar.data.models.Subject

@Composable
fun BookCard(
    book: Book,
    onDelete: () -> Unit,
    onClick: (String) -> Unit,
    onLoadImage: (String) -> String
){
    var expanded by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }

    var imageUrl by remember { mutableStateOf("") }

    if (book.image != null){
        imageUrl = onLoadImage(book.image)
    }

    Card(
        onClick = { onClick(book.id) },
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
                    .weight(0.25F),
                contentAlignment = Alignment.Center,
            ){
                if (imageUrl == ""){
                    Image(
                        Icons.Outlined.Image,
                        contentDescription = "Placeholder picture",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .fillMaxSize()
                    )
                }else{
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
            Column (
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .weight(0.65F)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5F)
                    .padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = book.title,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
                    .padding(start = 10.dp)
                    .weight(0.5F),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "${stringResource(R.string.generic_author)}: ${book.author}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.5f)
                    )
                    if (book.subject != null){
                        val subject = stringResource(Subject.valueOf(book.subject.uppercase()).label)
                        Text(
                            text = "${stringResource(R.string.generic_subject)}: ${subject}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(0.1F)){
                IconButton(
                    onClick = { expanded = !expanded },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Icon(Icons.Filled.MoreVert, "More")
                    DropdownMenu(
                            expanded = expanded,
                    onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Delete, "delete") },
                            text = { Text(stringResource(R.string.generic_delete)) },
                            onClick = {
                                showAlert = true
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (showAlert){
                GenericAlertDialog(
                    title = stringResource(R.string.alert_delete),
                    text = stringResource(R.string.alert_delete_desc),
                    confirmText = stringResource(R.string.generic_confirm),
                    confirmAction = {
                        onDelete()
                        showAlert = false
                                    },
                    dismissText = stringResource(R.string.generic_cancel),
                    dismissAction = { showAlert = false },
                    onDismissRequest = { showAlert = false },
                    icon = Icons.Default.Delete
                )
            }
        }
    }
}

