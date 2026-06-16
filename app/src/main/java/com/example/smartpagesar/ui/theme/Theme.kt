package com.example.smartpagesar.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6D9DEC),
    onPrimary = Color(0xFF002F6C),
    primaryContainer = Color(0xFF004A9F),
    onPrimaryContainer = Color(0xFFD6E2FF),

    secondary = Color(0xFF08224D),
    onSecondary = Color(0xFFE4F2FF),
    secondaryContainer = Color(0xFF004164),
    onSecondaryContainer = Color(0xFFB2EBF2),

    tertiary = Color(0xFFFFF4DF),
    onTertiary = Color(0xFF053683),
    tertiaryContainer = Color(0xFFB37E2E),
    onTertiaryContainer = Color(0xFFEDE7FF),

    background = Color(0xFF17202A),
    surface = Color(0xFF1A1C1E),
    outline = Color(0xFF5F6368)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4783E7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E2FF),
    onPrimaryContainer = Color(0xFF001A43),

    secondary = Color(0xFF0E6CDE),
    onSecondary = Color(0xFFE2FBFF),
    secondaryContainer = Color(0xFFE7FBFF),
    onSecondaryContainer = Color(0xFF023A3D),

    tertiary = Color(0xFF15066B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5E3DA),
    onTertiaryContainer = Color(0xFF642B00),

    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    outline = Color(0xFF8A8A8A)
)

@Composable
fun SmartPagesARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.navigationBarColor = colorScheme.primary.toArgb()
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}