package com.example.smartpagesar.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.vector.ImageVector

enum class InteractiveModelType(val icon: ImageVector) {
    STATIC(Icons.Default.ViewInAr),
    ANIMATION(Icons.Default.Animation),
    PARTS(Icons.Default.Extension),
    SECTION(Icons.Default.Splitscreen),
    BUILD(Icons.Default.Build)
}