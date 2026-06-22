package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomDescription(stringId: Int, fontSize: Int = 16){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 7.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "information",
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.weight(0.10F)
        )
        Text(
            text = stringResource(stringId),
            modifier = Modifier
                .weight(0.90F),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 16.sp,
            lineHeight = fontSize.sp
        )
    }
}