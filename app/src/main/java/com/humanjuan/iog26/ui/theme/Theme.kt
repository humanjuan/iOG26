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

enum class AppThemeOption { GREEN, NAVY, SUNSET, VIOLET }

// GREEN
private val DarkGreen = darkColorScheme(
    primary = Oxley,
    onPrimary = OnPrimaryDark,
    primaryContainer = Spectra,
    onPrimaryContainer = OnPrimaryDark,

    secondary = Como,
    onSecondary = OnPrimaryDark,
    secondaryContainer = TePapaGreen,
    onSecondaryContainer = OnPrimaryDark,

    tertiary = MossGreen,
    onTertiary = Color(0xFF103025),

    background = TePapaGreen,
    onBackground = MossGreen,
    surface = Spectra,
    onSurface = MossGreen
)

private val LightGreen = lightColorScheme(
    primary = Como,
    onPrimary = OnPrimaryLight,
    primaryContainer = MossGreen,
    onPrimaryContainer = Color(0xFF19332F),

    secondary = Oxley,
    onSecondary = OnPrimaryLight,
    secondaryContainer = MossGreen,
    onSecondaryContainer = Color(0xFF0F2A26),

    tertiary = Spectra,
    onTertiary = OnPrimaryLight,

    background = SurfaceLight,
    onBackground = Color(0xFF102323),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF102323)
)

// NAVY
private val DarkNavy = darkColorScheme(
    primary = NavyAccent,
    onPrimary = Color.White,
    primaryContainer = Navy700,
    onPrimaryContainer = NavyOn,

    secondary = Navy600,
    onSecondary = Color.White,
    secondaryContainer = Navy800,
    onSecondaryContainer = NavyOn,

    tertiary = NavyOn,
    onTertiary = Navy900,

    background = Navy900,
    onBackground = NavyOn,
    surface = Navy800,
    onSurface = NavyOn
)

private val LightNavy = lightColorScheme(
    primary = NavyAccent,
    onPrimary = Color.White,
    primaryContainer = NavyOn,
    onPrimaryContainer = Navy800,

    secondary = Navy600,
    onSecondary = Color.White,
    secondaryContainer = Navy700,
    onSecondaryContainer = NavyOn,

    tertiary = Navy700,
    onTertiary = Color.White,

    background = Color(0xFFF4F6FF),
    onBackground = Color(0xFF0D1026),
    surface = Color.White,
    onSurface = Color(0xFF0D1026)
)

// SUNSET
private val DarkSunset = darkColorScheme(
    primary = SunsetPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = SunsetSecondary,
    onPrimaryContainer = SunsetOn,

    secondary = SunsetSecondary,
    onSecondary = SunsetOn,
    secondaryContainer = SunsetPrimaryDark,
    onSecondaryContainer = Color.White,

    tertiary = SunsetTertiary,
    onTertiary = SunsetOn,

    background = Color(0xFF1F1A18),
    onBackground = Color(0xFFFFE7DF),
    surface = Color(0xFF2B2320),
    onSurface = Color(0xFFFFE7DF)
)

private val LightSunset = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = Color.White,
    primaryContainer = SunsetSecondary,
    onPrimaryContainer = SunsetOn,

    secondary = SunsetSecondary,
    onSecondary = SunsetOn,
    secondaryContainer = SunsetTertiary,
    onSecondaryContainer = SunsetOn,

    tertiary = SunsetTertiary,
    onTertiary = SunsetOn,

    background = SunsetBg,
    onBackground = SunsetOn,
    surface = Color.White,
    onSurface = SunsetOn
)

// VIOLET
private val DarkViolet = darkColorScheme(
    primary = VioletPrimaryDark,
    onPrimary = VioletOn,
    primaryContainer = VioletSecondary,
    onPrimaryContainer = Color(0xFF1C1535),

    secondary = VioletSecondary,
    onSecondary = Color(0xFF1C1535),
    secondaryContainer = VioletPrimaryDark,
    onSecondaryContainer = VioletOn,

    tertiary = VioletTertiary,
    onTertiary = Color(0xFF1C1535),

    background = VioletBgDark,
    onBackground = VioletOn,
    surface = Color(0xFF242345),
    onSurface = VioletOn
)

private val LightViolet = lightColorScheme(
    primary = VioletPrimary,
    onPrimary = VioletOn,
    primaryContainer = VioletSecondary,
    onPrimaryContainer = Color(0xFF221B3C),

    secondary = VioletSecondary,
    onSecondary = Color(0xFF221B3C),
    secondaryContainer = VioletTertiary,
    onSecondaryContainer = Color(0xFF221B3C),

    tertiary = VioletTertiary,
    onTertiary = Color(0xFF221B3C),

    background = Color(0xFFF7F5FF),
    onBackground = Color(0xFF221B3C),
    surface = Color.White,
    onSurface = Color(0xFF221B3C)
)

@Composable
fun IOG26Theme(
    appTheme: AppThemeOption = AppThemeOption.GREEN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Use dynamic color only if explicitly requested; default to our brand palette
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
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}