package com.humanjuan.iog26.ui.theme

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

enum class AppThemeOption { GREEN, NAVY, SUNSET, VIOLET, IOG26, ROSE }

private val LightGreen = lightColorScheme(
    primary = Color(0xFF4C7B66),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2DB),
    onPrimaryContainer = Color(0xFF0E231C),

    secondary = Color(0xFF8C6856),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECE1DB),
    onSecondaryContainer = Color(0xFF2E1B12),

    tertiary = Color(0xFFD4C28C),
    onTertiary = Color(0xFF2A2308),

    background = Color(0xFFF8F8F6),
    onBackground = Color(0xFF1A1C1B),
    surface = Color.White,
    onSurface = Color(0xFF1B1C1B),
    outline = Color(0xFF7A867E)
)

private val DarkGreen = darkColorScheme(
    primary = Color(0xFF5F8A76),
    onPrimary = Color(0xFFEAF0EC),
    primaryContainer = Color(0xFF324A3F),
    onPrimaryContainer = Color(0xFFDCE5DC),

    secondary = Color(0xFF9C7A67),
    onSecondary = Color(0xFFF8ECE8),
    secondaryContainer = Color(0xFF3D3029),
    onSecondaryContainer = Color(0xFFE6DAD5),

    tertiary = Color(0xFFD6C79A),
    onTertiary = Color(0xFF1F1B0B),

    background = Color(0xFF121412),
    onBackground = Color(0xFFE4E3DF),
    surface = Color(0xFF181A18),
    onSurface = Color(0xFFDADAD5),
    outline = Color(0xFF6B756E)
)

// NAVY
private val LightNavy = lightColorScheme(
    primary = Color(0xFF2D4E72),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7EF),
    onPrimaryContainer = Color(0xFF111C28),

    secondary = Color(0xFFB98A56),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF1E1),
    onSecondaryContainer = Color(0xFF322313),

    tertiary = Color(0xFFD4AF37),
    onTertiary = Color(0xFF231A00),

    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF111316),
    surface = Color.White,
    onSurface = Color(0xFF111316),
    outline = Color(0xFF68768A)
)

private val DarkNavy = darkColorScheme(
    primary = Color(0xFF3C5E82),
    onPrimary = Color(0xFFE9EEF4),
    primaryContainer = Color(0xFF22354B),
    onPrimaryContainer = Color(0xFFDCE5EC),

    secondary = Color(0xFFB17C48),
    onSecondary = Color(0xFFFFF0E1),
    secondaryContainer = Color(0xFF3E2D1F),
    onSecondaryContainer = Color(0xFFE9D7C9),

    tertiary = Color(0xFFD4AF37),
    onTertiary = Color(0xFF231A00),

    background = Color(0xFF101214),
    onBackground = Color(0xFFE5E8EC),
    surface = Color(0xFF181A1D),
    onSurface = Color(0xFFDADDE2),
    outline = Color(0xFF5A6778)
)

// SUNSET
private val LightSunset = lightColorScheme(
    primary = Color(0xFFC86B2E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE8D9),
    onPrimaryContainer = Color(0xFF2B1205),

    secondary = Color(0xFF4B6B88),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5ECF3),
    onSecondaryContainer = Color(0xFF0F1B26),

    tertiary = Color(0xFFE6C58E),
    onTertiary = Color(0xFF221807),

    background = Color(0xFFFFFAF7),
    onBackground = Color(0xFF201813),
    surface = Color.White,
    onSurface = Color(0xFF201813),
    outline = Color(0xFFB48B6E)
)

private val DarkSunset = darkColorScheme(
    primary = Color(0xFFD07C48),
    onPrimary = Color(0xFFFFF1E9),
    primaryContainer = Color(0xFF543523),
    onPrimaryContainer = Color(0xFFEAD4C3),

    secondary = Color(0xFF557190),
    onSecondary = Color(0xFFE6EDF3),
    secondaryContainer = Color(0xFF28394D),
    onSecondaryContainer = Color(0xFFD3DEE8),

    tertiary = Color(0xFFE0B978),
    onTertiary = Color(0xFF241A07),

    background = Color(0xFF151311),
    onBackground = Color(0xFFF1E3D8),
    surface = Color(0xFF1C1A18),
    onSurface = Color(0xFFE8DACC),
    outline = Color(0xFFA67E5D)
)

// VIOLET
private val LightViolet = lightColorScheme(
    primary = Color(0xFF8B5CB5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF2E8FF),
    onPrimaryContainer = Color(0xFF1D0E2D),

    secondary = Color(0xFF68A497),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2EE),
    onSecondaryContainer = Color(0xFF0E201A),

    tertiary = Color(0xFFD9BF85),
    onTertiary = Color(0xFF2B2009),

    background = Color(0xFFF9F7FC),
    onBackground = Color(0xFF20172B),
    surface = Color.White,
    onSurface = Color(0xFF20172B),

    outline = Color(0xFF9E8FC1),
    surfaceVariant = Color(0xFFF2EEF8),
    onSurfaceVariant = Color(0xFF4B435E)
)

private val DarkViolet = darkColorScheme(
    primary = Color(0xFF9F7AC7),
    onPrimary = Color(0xFFF8F3FF),
    primaryContainer = Color(0xFF3A2D4E),
    onPrimaryContainer = Color(0xFFE6D9F6),

    secondary = Color(0xFF5AA28C),
    onSecondary = Color(0xFFE5F3ED),
    secondaryContainer = Color(0xFF274038),
    onSecondaryContainer = Color(0xFFD7E8E1),

    tertiary = Color(0xFFD6B97B),
    onTertiary = Color(0xFF1F1706),

    background = Color(0xFF18171B),
    onBackground = Color(0xFFE8E0F4),
    surface = Color(0xFF211F24),
    onSurface = Color(0xFFDAD1E9),

    outline = Color(0xFF7C708F),
    surfaceVariant = Color(0xFF2A2830),
    onSurfaceVariant = Color(0xFFB9AEE0)
)

// IOG26
private val LightIOG26 = lightColorScheme(
    primary = Color(0xFFBD5312),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE8D9),
    onPrimaryContainer = Color(0xFF2B1205),

    secondary = Color(0xFF5E96A2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6EFF1),
    onSecondaryContainer = Color(0xFF071D21),

    tertiary = Color(0xFFD6B97B),
    onTertiary = Color(0xFF231B07),

    background = Color(0xFFF9F7F5),
    onBackground = Color(0xFF1E1915),
    surface = Color.White,
    onSurface = Color(0xFF1E1915),
    outline = Color(0xFF9C7C5B)
)

private val DarkIOG26 = darkColorScheme(
    primary = Color(0xFFB15A23),
    onPrimary = Color(0xFFFFEADA),
    primaryContainer = Color(0xFF4B2E18),
    onPrimaryContainer = Color(0xFFEED5BE),

    secondary = Color(0xFF4A7E8A),
    onSecondary = Color(0xFFE3EFF1),
    secondaryContainer = Color(0xFF25383B),
    onSecondaryContainer = Color(0xFFD2E0E2),

    tertiary = Color(0xFFDB8A54),
    onTertiary = Color(0xFF1E1309),

    background = Color(0xFF1A1816),
    onBackground = Color(0xFFE6C8A4),
    surface = Color(0xFF22201D),
    onSurface = Color(0xFFD7B691),
    outline = Color(0xFF9C7C5B)
)

// ROSE
private val LightRose = lightColorScheme(
    primary = Color(0xFFB76E79),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE8EB),
    onPrimaryContainer = Color(0xFF2B1012),

    secondary = Color(0xFF7BA7BB),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F2F6),
    onSecondaryContainer = Color(0xFF0F1E24),

    tertiary = Color(0xFFD6B97B),
    onTertiary = Color(0xFF231B07),

    background = Color(0xFFF9F4F4),
    onBackground = Color(0xFF221C1C),
    surface = Color.White,
    onSurface = Color(0xFF221C1C),
    outline = Color(0xFFA07E7D)
)

private val DarkRose = darkColorScheme(
    primary = Color(0xFFB77C85),
    onPrimary = Color(0xFFFFF1F2),
    primaryContainer = Color(0xFF4A3134),
    onPrimaryContainer = Color(0xFFEAD3D5),

    secondary = Color(0xFF6E8A9A),
    onSecondary = Color(0xFFE9F0F4),
    secondaryContainer = Color(0xFF2E3D44),
    onSecondaryContainer = Color(0xFFD7E0E5),

    tertiary = Color(0xFFD6B97B),
    onTertiary = Color(0xFF1E1607),

    background = Color(0xFF1A1718),
    onBackground = Color(0xFFEAD9DA),
    surface = Color(0xFF211E1F),
    onSurface = Color(0xFFDCC6C4),
    outline = Color(0xFFA07E7D)
)



@Composable
fun IOG26Theme(
    appTheme: AppThemeOption = AppThemeOption.GREEN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> when (appTheme) {
            AppThemeOption.GREEN -> if (darkTheme) DarkGreen else LightGreen
            AppThemeOption.NAVY -> if (darkTheme) DarkNavy else LightNavy
            AppThemeOption.SUNSET -> if (darkTheme) DarkSunset else LightSunset
            AppThemeOption.VIOLET -> if (darkTheme) DarkViolet else LightViolet
            AppThemeOption.IOG26 -> if (darkTheme) DarkIOG26 else LightIOG26
            AppThemeOption.ROSE -> if (darkTheme) DarkRose else LightRose
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}