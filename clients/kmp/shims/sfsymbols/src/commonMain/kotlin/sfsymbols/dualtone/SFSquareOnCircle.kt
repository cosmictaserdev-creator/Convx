package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareOnCircle (dualtone)
 * Viewport: 29.5996 x 28.7793
 */
public val SfSymbols.Dualtone.SFSquareOnCircle: ImageVector
    get() {
        if (_sFSquareOnCircle != null) {
            return _sFSquareOnCircle!!
        }
        _sFSquareOnCircle = sfIcon(
            name = "Dualtone.SFSquareOnCircle",
            viewportWidth = 29.5996f,
            viewportHeight = 28.7793f
        ) {
            addSfPath("M20.0457 7.33398L18.3341 7.33398C17.1761 3.97902 14.0263 1.64062 10.2344 1.64062C5.41016 1.64062 1.64062 5.41016 1.64062 10.2344C1.64062 14.2911 4.30622 17.6021 8.02734 18.5432L8.02734 20.2239C3.43354 19.2204 0 15.1301 0 10.2344C0 4.58008 4.58008 0 10.2344 0C14.8803 0 18.8009 3.09211 20.0457 7.33398Z", fillAlpha = 0.425f)
            addSfPath("M11.8262 27.5879L24.4824 27.5879C27.002 27.5879 28.2812 26.3281 28.2812 23.8281L28.2812 11.0938C28.2812 8.59375 27.002 7.33398 24.4824 7.33398L11.8262 7.33398C9.29688 7.33398 8.02734 8.59375 8.02734 11.0938L8.02734 23.8281C8.02734 26.3281 9.29688 27.5879 11.8262 27.5879ZM11.8555 25.8594C10.498 25.8594 9.75586 25.1367 9.75586 23.7305L9.75586 11.1816C9.75586 9.78516 10.498 9.0625 11.8555 9.0625L24.4434 9.0625C25.8105 9.0625 26.5527 9.78516 26.5527 11.1816L26.5527 23.7305C26.5527 25.1367 25.8105 25.8594 24.4434 25.8594Z", fillAlpha = 0.85f)
        }
        return _sFSquareOnCircle!!
    }

private var _sFSquareOnCircle: ImageVector? = null
