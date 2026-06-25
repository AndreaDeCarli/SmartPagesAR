package com.example.smartpagesar.ui.composables

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpagesar.data.models.InteractiveModel
import com.example.smartpagesar.data.models.InteractiveModelType
import java.io.File


@Composable
fun ModelCard(
    model: InteractiveModel,
    shortId: Int,
    context: Context
){
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 5.dp, vertical = 6.dp),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column( modifier = Modifier.weight(0.55f)) { Text(model.id + ".glb", fontSize = 20.sp)}
            Column( modifier = Modifier.weight(0.30f)) {
                Text(InteractiveModelType.entries[model.type].toString(), fontSize = 12.sp)}
            Column(modifier = Modifier.weight(0.15f)) {
                if (File(context.filesDir, "book$shortId/${model.id}.glb").exists()){
                    Icon(Icons.Default.Folder, "storage")
                }
            }

        }
    }
}