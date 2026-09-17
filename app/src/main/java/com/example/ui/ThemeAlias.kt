package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.ui.theme.JarvisBugTrackerTheme as ThemeImpl

@Composable
fun JarvisBugTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ThemeImpl(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
