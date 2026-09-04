package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme =
  darkColorScheme(
    primary = SoftCyan,
    secondary = SunnyYellow,
    tertiary = BubblePink,
    background = Color(0xFF1E1F29),
    surface = Color(0xFF282A3A),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SkyBlue,
    secondary = BrightOrange,
    tertiary = BubblePink,
    background = KidsBackgroundLight,
    surface = KidsSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = KidsTextDark,
    onSurface = KidsTextDark,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

