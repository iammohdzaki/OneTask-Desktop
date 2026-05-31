package com.one.task.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import onetask.shared.generated.resources.Res.font
import onetask.shared.generated.resources.sans_bold
import onetask.shared.generated.resources.sans_medium
import onetask.shared.generated.resources.sans_regular
import onetask.shared.generated.resources.sans_thin
import org.jetbrains.compose.resources.Font

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    primaryContainer = Color(0xFFA078FF),
    onPrimaryContainer = Color(0xFF340080),
    secondary = Color(0xFFADC6FF),
    tertiary = Color(0xFFFFB869),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    background = Color(0xFF131315),
    onBackground = Color(0xFFE5E1E4),
    surface = Color(0xFF131315),
    onSurface = Color(0xFFE5E1E4),
    surfaceVariant = Color(0xFF353437),
    onSurfaceVariant = Color(0xFFCBC3D7),
    surfaceContainerLowest = Color(0xFF0E0E10),
    surfaceContainerLow = Color(0xFF1B1B1D),
    surfaceContainer = Color(0xFF201F21),
    surfaceContainerHigh = Color(0xFF2A2A2C),
    surfaceContainerHighest = Color(0xFF353437),
    outline = Color(0xFF958EA0),
    outlineVariant = Color(0xFF494454)
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F2F9),
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

@Composable
fun SansFontFamily() = FontFamily(
    Font(font.sans_thin, FontWeight.Light),
    Font(font.sans_regular, FontWeight.Normal),
    Font(font.sans_medium, FontWeight.Medium),
    Font(font.sans_bold, FontWeight.Bold)
)

@Composable
fun SansTypography() = Typography().run {
    val fontFamily = SansFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}

@Composable
fun OneTaskTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SansTypography(),
        content = content
    )
}
