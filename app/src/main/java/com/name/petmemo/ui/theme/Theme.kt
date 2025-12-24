package com.name.petmemo.ui.theme

import androidx.activity.ComponentActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.name.petmemo.R

// --- ВСЕ ЦВЕТОВЫЕ СХЕМЫ ---

// 0. Главная тема
private val PetMemoDefaultLightColorScheme = lightColorScheme(
    primary = OriginalPetPurple,
    onPrimary = OriginalTextLight, // Белый текст на фиолетовом (primary)
    primaryContainer = OriginalDarkPetPurple,
    onPrimaryContainer = OriginalTextLight, // Белый текст на темном фиолетовом

    secondary = OriginalAccentGreen,
    onSecondary = OriginalTextLight, // Белый текст на зеленом (secondary)
    secondaryContainer = OriginalAccentGreen.copy(alpha = 0.7f),
    onSecondaryContainer = OriginalTextLight, // Белый текст на полупрозрачном зеленом

    background = OriginalBackgroundLight, // Светлый фон
    onBackground = OriginalTextLight, // Темный текст на светлом фоне

    surface = OriginalSurfaceLight.copy(alpha = 0.7f), // Светлая поверхность (карточки)
    onSurface = OriginalTextLight, // Темный текст на светлой поверхности

    surfaceVariant = OriginalOutline.copy(alpha = 0.2f),
    onSurfaceVariant = OriginalTextLight.copy(alpha = 0.7f), // Второстепенный темный текст на светлой поверхности

    tertiary = OriginalLightBlue,
    tertiaryContainer = OriginalPetPinkGlow,
    error = OriginalError,
    onError = OriginalTextLight,
    outline = OriginalOutline
)

private val PetMemoDefaultDarkColorScheme = darkColorScheme(
    primary = OriginalPetPurple,
    onPrimary = Color.White,
    primaryContainer = OriginalDarkPetPurple,
    onPrimaryContainer = Color.White,

    secondary = OriginalAccentGreen,
    onSecondary = Color.White,
    secondaryContainer = OriginalAccentGreen.copy(alpha = 0.7f),
    onSecondaryContainer = Color.White,

    background = OriginalBackgroundDark, // Темный фон
    onBackground = OriginalTextLight, // Светлый текст на темном фоне

    surface = OriginalSurfaceDark, // Темная поверхность (карточки)
    onSurface = OriginalTextLight, // Светлый текст на темной поверхности

    surfaceVariant = OriginalOutline.copy(alpha = 0.2f),
    onSurfaceVariant = OriginalTextLight.copy(alpha = 0.7f), // Второстепенный светлый текст на темной поверхности

    tertiary = OriginalLightBlue,
    tertiaryContainer = OriginalPetPinkGlow,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

// 1. Тема "Зефир"
private val ZephyrLightColorScheme = lightColorScheme(
    primary = ZephyrPrimary,
    onPrimary = Color.White, // БЕЛЫЙ текст на розовом фоне (ZephyrPrimary)

    primaryContainer = ZephyrPrimaryContainer,
    onPrimaryContainer = Color.White, // БЕЛЫЙ текст на ZephyrPrimaryContainer (ваш цвет)

    secondary = ZephyrOnPrimaryContainer, // Это был цвет, теперь фон.
    onSecondary = Color.White, // БЕЛЫЙ текст на secondary

    secondaryContainer = ZephyrOnPrimaryContainer.copy(alpha = 0.6f),
    onSecondaryContainer = Color.White, // БЕЛЫЙ текст на secondaryContainer

    background = ZephyrBackgroundLight, // Светлый фон
    onBackground = Color.White, // БЕЛЫЙ текст на светлом фоне (ZephyrBackgroundLight)

    surface = ZephyrSurfaceLight, // Светлая поверхность
    onSurface = Color.White, // БЕЛЫЙ текст на светлой поверхности (ZephyrSurfaceLight)

    surfaceVariant = Color.White.copy(alpha = 0.3f),
    onSurfaceVariant = Color.White, // БЕЛЫЙ текст на surfaceVariant (полупрозрачный белый)

    tertiary = ZephyrSecondary,
    tertiaryContainer = ZephyrButtonColor,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

private val ZephyrDarkColorScheme = darkColorScheme(
    primary = ZephyrAccentDark,
    onPrimary = Color.Black, // Черный текст на акцентном синем (если фон светлый, но primary обычно темный в темной теме)
    // **Пересмотрите: возможно, тут должен быть ZephyrTextDark, если ZephyrAccentDark темный**
    primaryContainer = ZephyrPrimary,
    onPrimaryContainer = Color.Black, // **Пересмотрите**

    secondary = ZephyrPrimary,
    onSecondary = Color.Black, // **Пересмотрите**
    secondaryContainer = ZephyrPrimary.copy(alpha = 0.6f),
    onSecondaryContainer = Color.Black, // **Пересмотрите**

    background = ZephyrBackgroundDark, // Темный фон
    onBackground = ZephyrTextDark, // Светлый текст на темном фоне

    surface = ZephyrSurfaceDark, // Темная поверхность
    onSurface = ZephyrTextDark, // Светлый текст на темной поверхности

    surfaceVariant = ZephyrSurfaceDark.copy(alpha = 0.8f),
    onSurfaceVariant = ZephyrTextDark.copy(alpha = 0.7f),

    tertiary = ZephyrWarning,
    tertiaryContainer = ZephyrButtonColor,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

// 2. Тема "Лес"
private val ForestLightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = Color.White, // БЕЛЫЙ текст на темно-зеленом (ForestPrimary)
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = Color.White, // БЕЛЫЙ текст на ForestPrimaryContainer

    secondary = ForestSecondary,
    onSecondary = Color.White, // БЕЛЫЙ текст на коричневом (ForestSecondary)
    secondaryContainer = ForestSecondary.copy(alpha = 0.6f),
    onSecondaryContainer = Color.White, // БЕЛЫЙ текст на secondaryContainer

    background = ForestBackgroundLight, // Светлый фон
    onBackground = Color.White, // БЕЛЫЙ текст на светлом фоне (ForestBackgroundLight) - ВОЗМОЖНО, НЕВИДИМ!

    surface = ForestSurfaceLight, // Светлая поверхность
    onSurface = Color.White, // БЕЛЫЙ текст на светлой поверхности (ForestSurfaceLight) - ВОЗМОЖНО, НЕВИДИМ!

    surfaceVariant = ForestSurfaceLight.copy(alpha = 0.8f),
    onSurfaceVariant = Color.White, // БЕЛЫЙ текст на surfaceVariant

    tertiary = ForestAccentDark,
    tertiaryContainer = ForestPrimaryContainer,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

private val ForestDarkColorScheme = darkColorScheme(
    primary = ForestAccentDark,
    onPrimary = ForestTextDark, // Светлый текст на акцентном зеленом
    primaryContainer = ForestPrimary,
    onPrimaryContainer = ForestTextDark, // Светлый текст на темно-зеленом

    secondary = ForestSecondary,
    onSecondary = ForestTextDark, // Светлый текст на коричневом
    secondaryContainer = ForestSecondary.copy(alpha = 0.6f),
    onSecondaryContainer = ForestTextDark, // Светлый текст на коричневом

    background = ForestBackgroundDark, // Темный фон
    onBackground = ForestTextDark, // Светлый текст на темном фоне

    surface = ForestSurfaceDark, // Темная поверхность
    onSurface = ForestTextDark, // Светлый текст на темной поверхности

    surfaceVariant = ForestSurfaceDark.copy(alpha = 0.8f),
    onSurfaceVariant = ForestTextDark.copy(alpha = 0.7f),

    tertiary = ForestPrimary,
    tertiaryContainer = ForestAccentDark,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

// 3. Тема "Океан"
private val OceanLightColorScheme = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White, // БЕЛЫЙ текст на темно-синем (OceanPrimary)
    primaryContainer = OceanPrimaryContainer,
    onPrimaryContainer = Color.White, // БЕЛЫЙ текст на OceanPrimaryContainer

    secondary = OceanAccentDark,
    onSecondary = Color.White, // БЕЛЫЙ текст на акцентном голубом (OceanAccentDark)
    secondaryContainer = OceanGradientBottom.copy(alpha = 0.7f),
    onSecondaryContainer = Color.White, // БЕЛЫЙ текст на secondaryContainer

    background = OceanBackgroundLight, // Светлый фон
    onBackground = Color.White, // БЕЛЫЙ текст на светлом фоне (OceanBackgroundLight) - ВОЗМОЖНО, НЕВИДИМ!

    surface = OceanSurfaceLight, // Светлая поверхность
    onSurface = Color.White, // БЕЛЫЙ текст на светлой поверхности (OceanSurfaceLight) - ВОЗМОЖНО, НЕВИДИМ!

    surfaceVariant = Color.White.copy(alpha = 0.3f),
    onSurfaceVariant = Color.White, // БЕЛЫЙ текст на surfaceVariant

    tertiary = OceanSecondary,
    tertiaryContainer = OceanPrimaryContainer,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)

private val OceanDarkColorScheme = darkColorScheme(
    primary = OceanAccentDark,
    onPrimary = OceanTextDark, // Светлый текст на акцентном голубом
    primaryContainer = OceanPrimary,
    onPrimaryContainer = OceanTextDark, // Светлый текст на темно-синем

    secondary = OceanSecondary,
    onSecondary = OceanTextDark, // Светлый текст на бирюзовом
    secondaryContainer = OceanSecondary.copy(alpha = 0.6f),
    onSecondaryContainer = OceanTextDark, // Светлый текст на бирюзовом

    background = OceanBackgroundDark, // Темный фон
    onBackground = OceanTextDark, // Светлый текст на темном фоне

    surface = OceanSurfaceDark, // Темная поверхность
    onSurface = OceanTextDark, // Светлый текст на темной поверхности

    surfaceVariant = OceanSurfaceDark.copy(alpha = 0.8f),
    onSurfaceVariant = OceanTextDark.copy(alpha = 0.7f),

    tertiary = OceanPrimary,
    tertiaryContainer = OceanAccentDark,
    error = OriginalError,
    onError = Color.White,
    outline = OriginalOutline
)


// --- ВСЕ ГРАДИЕНТЫ ---
val defaultThemeGradient = Brush.verticalGradient(listOf(OriginalPetPurple, OriginalAccentGreen))
val zephyrGradient = Brush.verticalGradient(listOf(ZephyrPrimary.copy(alpha = 0.9f), ZephyrSecondary.copy(alpha = 0.9f)))
val forestGradient = Brush.verticalGradient(listOf(ForestPrimary.copy(alpha = 0.9f), ForestSecondary.copy(alpha = 0.9f)))
val oceanGradient = Brush.verticalGradient(listOf(OceanPrimary.copy(alpha = 0.9f), OceanSecondary.copy(alpha = 0.9f)))


// --- ЕДИНЫЙ ПЕРЕКЛЮЧАТЕЛЬ ТЕМ (ENUM) ---
enum class AppTheme(
    val id: Int,
    val themeName: String,
    val lightColors: ColorScheme,
    val darkColors: ColorScheme,
    val patternResId: Int? = null
) {
    DEFAULT(
        id = 1,
        themeName = "Базовая",
        lightColors = PetMemoDefaultLightColorScheme,
        darkColors = PetMemoDefaultDarkColorScheme,
        patternResId = R.drawable.ic_paw_background_pattern
    ),
    ZEPHYR(
        id = 2,
        themeName = "Розовая",
        lightColors = ZephyrLightColorScheme,
        darkColors = ZephyrDarkColorScheme,
        patternResId = R.drawable.ic_pattern_clouds
    ),
    FOREST(
        id = 3,
        themeName = "Лесная",
        lightColors = ForestLightColorScheme,
        darkColors = ForestDarkColorScheme,
        patternResId = R.drawable.ic_pattern_leaves
    ),
    OCEAN(
        id = 4,
        themeName = "Небесная",
        lightColors = OceanLightColorScheme,
        darkColors = OceanDarkColorScheme,
        patternResId = R.drawable.ic_pattern_waves
    )
}
@Composable
fun PetMemoTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = AppTheme.DEFAULT.lightColors
    val view = LocalView.current
    val context = LocalContext.current as ComponentActivity

    SideEffect {
        val window = context.window
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = colorScheme.primary.luminance() > 0.5f
        insetsController.isAppearanceLightNavigationBars = colorScheme.background.luminance() > 0.5f
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}