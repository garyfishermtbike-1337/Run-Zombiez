package com.rangerdie.runzombiez.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Run! Zombiez is dark-only by design (spec section 21) — no light theme variant.
private val RunZombiezColorScheme = darkColorScheme(
    primary = WarningRed,
    onPrimary = BoneWhite,
    secondary = EmergencyAmber,
    onSecondary = HavenBlack,
    tertiary = StaticGreen,
    background = HavenBlack,
    onBackground = BoneWhite,
    surface = AshGray,
    onSurface = BoneWhite,
    surfaceVariant = AshGrayLight,
    onSurfaceVariant = BoneWhite,
    error = WarningRed,
    onError = BoneWhite
)

@Composable
fun RunZombiezTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RunZombiezColorScheme,
        typography = RunZombiezTypography,
        content = content
    )
}
