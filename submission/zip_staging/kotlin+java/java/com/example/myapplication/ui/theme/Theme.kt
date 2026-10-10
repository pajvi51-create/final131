package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SpacePurple, onPrimary = CloudWhite,
    primaryContainer = Lavender, onPrimaryContainer = SpacePurpleDark,
    secondary = ToyGreenDark, onSecondary = CloudWhite,
    secondaryContainer = Color(0xFFE2F5B8), onSecondaryContainer = Color(0xFF173800),
    tertiary = ToyGreen,
    background = CloudWhite, onBackground = Ink,
    surface = CloudWhite, onSurface = Ink,
    surfaceVariant = SoftLilac, onSurfaceVariant = MutedInk,
    outline = Color(0xFF817586)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD4B8FF), onPrimary = SpacePurpleDark,
    primaryContainer = SpacePurple,
    secondary = Color(0xFFB6ED65), onSecondary = Color(0xFF173800),
    secondaryContainer = ToyGreenDark,
    background = NightSurface, onBackground = CloudWhite,
    surface = NightCard, onSurface = CloudWhite,
    surfaceVariant = Color(0xFF352943), onSurfaceVariant = Color(0xFFD2C4DC)
)

@Composable
fun MyApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, typography = Typography, content = content)
}
