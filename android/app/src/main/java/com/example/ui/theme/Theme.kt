package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GateDarkColorScheme = darkColorScheme(
    primary = SolidGateOrange,
    onPrimary = Color.White,
    primaryContainer = SolidGateOrange,
    onPrimaryContainer = Color.White,
    secondary = SolidGateCyan,
    onSecondary = Color.Black,
    secondaryContainer = SolidGateCyan,
    onSecondaryContainer = Color.Black,
    tertiary = SolidPurple,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force sleek modern dark aesthetic as shown in reference image
    dynamicColor: Boolean = false, // Keep intentional solid color accents
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GateDarkColorScheme,
        typography = Typography,
        content = content
    )
}
