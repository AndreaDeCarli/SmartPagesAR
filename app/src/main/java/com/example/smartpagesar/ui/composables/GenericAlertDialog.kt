package com.example.smartpagesar.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun GenericAlertDialog(
    title: String,
    text: String,
    confirmText: String,
    confirmAction: () -> Unit,
    dismissText: String = "",
    dismissAction: () -> Unit = {},
    onDismissRequest: () -> Unit,
    icon: ImageVector
) {
    AlertDialog(
        icon = {
            Icon(
                icon, contentDescription = "Icon",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        },
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground)
        },
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = confirmAction,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            if (dismissText != "") {
                TextButton(
                    onClick = dismissAction,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.background,
                        disabledContentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(dismissText)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    )
}