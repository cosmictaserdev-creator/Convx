package zone.ien.hig.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun rememberDefaultBackdrop(): LayerBackdrop {
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) Color.Black else Color.White
    return rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
}