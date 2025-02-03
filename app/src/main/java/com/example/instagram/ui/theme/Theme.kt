package com.example.instagram.ui.theme

import android.app.Activity
import com.google.accompanist.systemuicontroller.rememberSystemUiController

import android.os.Build
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
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.example.instagram.R

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkThemeBg,
    onBackground = Color.White,
    onSurface = Color.White,
    surface = Color.White,
    surfaceVariant = lightBlack
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Color.Black,
    tertiary = Pink40,
    background = Color.White,
    onBackground = Color.Black,
    onSurface = TextColor,
    surface = Color.Gray,
    surfaceVariant = lightWhite

)

@Composable
fun InstagramTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val MystatusBarColor= if (darkTheme) colorResource(R.color.black) else colorResource(R.color.white)
    val view = LocalView.current
    if(!view.isInEditMode){
        systemUiController.setStatusBarColor(
            color = MystatusBarColor,
            darkIcons = !darkTheme // Light icons in dark mode, dark icons in light mode
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

