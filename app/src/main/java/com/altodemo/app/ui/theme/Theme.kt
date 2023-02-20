package com.altodemo.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val appContentMargin = 32.dp
val bottomFeatureButtonMargin = 16.dp
val topTitlePadding = 25.dp

private val DarkColorScheme = darkColorScheme(
    background = AltoDemoColor.Brown80,
    primary = AltoDemoColor.Brown80,
    secondary = AltoDemoColor.Tan10,
    onBackground = AltoDemoColor.White,
    surface = AltoDemoColor.Grey15,
    onSurface = AltoDemoColor.Brown80
)

private val LightColorScheme = lightColorScheme(
    background = AltoDemoColor.Tan10,
    onBackground = AltoDemoColor.Black90,
    primary = AltoDemoColor.Tan10,
    secondary = AltoDemoColor.LeatherTan50,
    surface = AltoDemoColor.Grey15,
    onSurface = AltoDemoColor.Brown80
)

@Composable
fun AltoDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    val currentWindow = (view.context as? Activity)?.window ?: return
    if (!view.isInEditMode) {
        SideEffect {
            /* the default code did the same cast here - might as well use our new variable! */
            currentWindow.statusBarColor = AltoDemoColor.Black100.copy(alpha = 0.2f).toArgb()
            /* accessing the insets controller to change appearance of the status bar, with 100% less deprecation warnings */
            WindowCompat.getInsetsController(currentWindow, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AltoTypography,
        content = content
    )
}