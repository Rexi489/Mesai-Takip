package com.mesaihesap.app.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnDark,
    primaryContainer = PrimaryBlueLight,
    secondary = OvertimeOrange,
    background = SurfaceLight,
    surface = CardLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = TextOnDark,
    primaryContainer = PrimaryBlueDark,
    secondary = OvertimeOrangeLight,
    background = SurfaceDark,
    surface = CardDark,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
)

val LocalDarkTheme = staticCompositionLocalOf { false }

@Composable
fun MesaiHesapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
