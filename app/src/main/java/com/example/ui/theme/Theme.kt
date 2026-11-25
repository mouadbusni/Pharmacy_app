package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PharmacyColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = PureWhite,
    primaryContainer = MintSoft,
    onPrimaryContainer = ForestGreen,
    secondary = MintTeal,
    onSecondary = PureWhite,
    secondaryContainer = MintSurfaceTint,
    onSecondaryContainer = ForestGreen,
    tertiary = WarmGold,
    onTertiary = TextPrimary,
    background = NeutralBackground,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = NeutralBackground,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = StatusClosedRed,
    onError = PureWhite
)

private val PharmacyShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SaydaliyatiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PharmacyColorScheme,
        typography = Typography,
        shapes = PharmacyShapes,
        content = content
    )
}
