package com.tanguymichardiere.sad_cat_project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.tanguymichardiere.sad_cat_project.DARK_THEME
import com.tanguymichardiere.sad_cat_project.dataStore
import kotlinx.coroutines.flow.map

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun SadCatTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME] ?: isSystemInDarkTheme
    }.collectAsState(initial = isSystemInDarkTheme).value

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
