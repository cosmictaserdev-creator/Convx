package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionProBadgePlayFill (dualtone)
 * Viewport: 43.0469 x 28.1934
 */
public val SfSymbols.Dualtone.SFVisionProBadgePlayFill: ImageVector
    get() {
        if (_sFVisionProBadgePlayFill != null) {
            return _sFVisionProBadgePlayFill!!
        }
        _sFVisionProBadgePlayFill = sfIcon(
            name = "Dualtone.SFVisionProBadgePlayFill",
            viewportWidth = 43.0469f,
            viewportHeight = 28.1934f
        ) {
            addSfPath("M28.1723 4.31547C27.6159 5.38337 27.3047 6.59388 27.3047 7.87109C27.3047 12.1973 30.8496 15.7422 35.1758 15.7422C36.522 15.7422 37.7973 15.3913 38.9137 14.777C38.6006 19.8556 35.2709 23.6133 30.918 23.6133C26.1914 23.6133 23.7402 19.5996 21.3379 19.5996C18.9355 19.5996 16.4844 23.6133 11.7578 23.6133C7.17773 23.6133 3.73047 19.4531 3.73047 13.9746C3.73047 5.01953 11.6016 4.02344 21.3379 4.02344C23.7629 4.02344 26.0721 4.08462 28.1723 4.31547Z", fillAlpha = 0.425f)
            addSfPath("M41.4941 7.87109C41.4941 11.3281 38.6035 14.1895 35.1758 14.1895C31.7188 14.1895 28.8574 11.3477 28.8574 7.87109C28.8574 4.41406 31.7188 1.55273 35.1758 1.55273C38.6426 1.55273 41.4941 4.4043 41.4941 7.87109ZM33.1152 5.21484L33.1152 10.5078C33.1152 10.9863 33.5938 11.1816 34.0137 10.9375L38.3398 8.36914C38.75 8.13477 38.7207 7.59766 38.2812 7.33398L34.0137 4.78516C33.6035 4.54102 33.1152 4.73633 33.1152 5.21484Z", fillAlpha = 0.85f)
        }
        return _sFVisionProBadgePlayFill!!
    }

private var _sFVisionProBadgePlayFill: ImageVector? = null
