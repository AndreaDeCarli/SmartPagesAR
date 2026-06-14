package com.example.smartpagesar.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomDivider(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.width(12.dp).padding(vertical = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground)
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp))
                .padding(vertical = 0.dp, horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onBackground)
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground)
    }
}