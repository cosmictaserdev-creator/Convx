package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCirclebadge2Fill (monochrome)
 * Viewport: 30.2051 x 22.4316
 */
public val SfSymbols.Monochrome.SFCirclebadge2Fill: ImageVector
    get() {
        if (_sFCirclebadge2Fill != null) {
            return _sFCirclebadge2Fill!!
        }
        _sFCirclebadge2Fill = sfIcon(
            name = "Monochrome.SFCirclebadge2Fill",
            viewportWidth = 30.2051f,
            viewportHeight = 22.4316f
        ) {
            addSfPath("M28.2812 11.2109C28.2812 16.5332 23.9551 20.8594 18.6328 20.8594C18.1098 20.8594 17.5964 20.8176 17.0996 20.7257C20.2946 18.76 22.4219 15.2312 22.4219 11.2109C22.4219 7.18928 20.2932 3.65535 17.0963 1.68719C17.5942 1.59479 18.1087 1.55273 18.6328 1.55273C23.9551 1.55273 28.2812 5.88867 28.2812 11.2109Z", fillAlpha = 0.85f)
            addSfPath("M11.2109 20.8594C16.5332 20.8594 20.8594 16.5332 20.8594 11.2109C20.8594 5.88867 16.5332 1.55273 11.2109 1.55273C5.88867 1.55273 1.5625 5.88867 1.5625 11.2109C1.5625 16.5332 5.88867 20.8594 11.2109 20.8594Z", fillAlpha = 0.85f)
        }
        return _sFCirclebadge2Fill!!
    }

private var _sFCirclebadge2Fill: ImageVector? = null
