import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.name.petmemo.ui.theme.AppTheme

@Composable
fun AppBackground(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val useDarkTheme = isSystemInDarkTheme()
    val colors = if (useDarkTheme) appTheme.darkColors else appTheme.lightColors
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            colors.primary,
            colors.secondary
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush)) {
        appTheme.patternResId?.let { patternId ->
            Image(
                painter = painterResource(id = patternId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,

                alpha = 0.08f,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f))
            )
        }
        content()
    }
}