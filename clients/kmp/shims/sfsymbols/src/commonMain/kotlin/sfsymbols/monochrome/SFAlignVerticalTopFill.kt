package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignVerticalTopFill (monochrome)
 * Viewport: 29.0039 x 27.666
 */
public val SfSymbols.Monochrome.SFAlignVerticalTopFill: ImageVector
    get() {
        if (_sFAlignVerticalTopFill != null) {
            return _sFAlignVerticalTopFill!!
        }
        _sFAlignVerticalTopFill = sfIcon(
            name = "Monochrome.SFAlignVerticalTopFill",
            viewportWidth = 29.0039f,
            viewportHeight = 27.666f
        ) {
            addSfPath("M5.84961 27.666L10.4492 27.666C12.1094 27.666 13.0371 26.7285 13.0371 25.0488L13.0371 6.64062C13.0371 4.96094 12.1094 4.02344 10.4492 4.02344L5.84961 4.02344C4.19922 4.02344 3.27148 4.96094 3.27148 6.64062L3.27148 25.0488C3.27148 26.7285 4.19922 27.666 5.84961 27.666ZM18.2129 18.9355L22.8223 18.9355C24.4727 18.9355 25.4004 17.998 25.4004 16.3086L25.4004 6.64062C25.4004 4.96094 24.4727 4.02344 22.8223 4.02344L18.2129 4.02344C16.5625 4.02344 15.6348 4.96094 15.6348 6.64062L15.6348 16.3086C15.6348 17.998 16.5625 18.9355 18.2129 18.9355Z", fillAlpha = 0.85f)
            addSfPath("M0.751953 1.62109L27.8906 1.62109C28.3105 1.62109 28.6426 1.25 28.6426 0.820312C28.6426 0.400391 28.3105 0.0292969 27.8906 0.0292969L0.751953 0.0292969C0.332031 0.0292969 0 0.400391 0 0.820312C0 1.25 0.332031 1.62109 0.751953 1.62109Z", fillAlpha = 0.85f)
        }
        return _sFAlignVerticalTopFill!!
    }

private var _sFAlignVerticalTopFill: ImageVector? = null
