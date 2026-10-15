package com.birtucansoftware.birtusince.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun BirtuTheme(
    preset: ThemePreset = ThemePreset.Tangelo,
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val targetColors = when (preset) {
        ThemePreset.Tangelo -> if (isDarkMode) TangeloDarkColors else TangeloLightColors
        ThemePreset.Arctic -> if (isDarkMode) ArcticDarkColors else ArcticLightColors
        ThemePreset.Obsidian -> if (isDarkMode) ObsidianDarkColors else ObsidianLightColors
        ThemePreset.Amber -> if (isDarkMode) AmberDarkColors else AmberLightColors
        ThemePreset.Emerald -> if (isDarkMode) EmeraldDarkColors else EmeraldLightColors
        ThemePreset.Bubblegum -> if (isDarkMode) BubblegumDarkColors else BubblegumLightColors
    }

    val animatedColors = targetColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(targetColors.isDark, targetColors.bg, targetColors.surface) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = targetColors.bg.toArgb()
                window.navigationBarColor = targetColors.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !targetColors.isDark
                insetsController.isAppearanceLightNavigationBars = !targetColors.isDark
            }
        }
    }

    val m3ColorScheme = if (targetColors.isDark) {
        darkColorScheme(
            primary = targetColors.primary,
            onPrimary = targetColors.onPrimary,
            primaryContainer = targetColors.primarySoft,
            onPrimaryContainer = targetColors.primaryStrong,
            background = targetColors.bg,
            surface = targetColors.surface,
            surfaceVariant = targetColors.surfaceRaised,
            outline = targetColors.outline,
            error = targetColors.late,
            onError = targetColors.onLate
        )
    } else {
        lightColorScheme(
            primary = targetColors.primary,
            onPrimary = targetColors.onPrimary,
            primaryContainer = targetColors.primarySoft,
            onPrimaryContainer = targetColors.primaryStrong,
            background = targetColors.bg,
            surface = targetColors.surface,
            surfaceVariant = targetColors.surfaceRaised,
            outline = targetColors.outline,
            error = targetColors.late,
            onError = targetColors.onLate
        )
    }

    CompositionLocalProvider(LocalBirtuColors provides animatedColors) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            content = content
        )
    }
}
