package app.muukii.androidplayground

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Shares the iOS playground's palette while following the system appearance. */
@Composable
fun PlaygroundTheme(content: @Composable () -> Unit) {
    val scheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = Color(0xFF7BD6E2),
            onPrimary = Color(0xFF00363E),
            primaryContainer = Color(0xFF004F59),
            onPrimaryContainer = Color(0xFFB2EDF4),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF007A8A),
            primaryContainer = Color(0xFFD1EEF1),
            onPrimaryContainer = Color(0xFF00525D),
            onPrimary = Color.White,
            background = Color(0xFFF5F6F8),
            surface = Color.White,
            surfaceContainerHighest = Color(0xFFE4E5E8),
        )
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
