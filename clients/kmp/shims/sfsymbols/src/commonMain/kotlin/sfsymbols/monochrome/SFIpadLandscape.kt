package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpadLandscape (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFIpadLandscape: ImageVector
    get() {
        if (_sFIpadLandscape != null) {
            return _sFIpadLandscape!!
        }
        _sFIpadLandscape = sfIcon(
            name = "Monochrome.SFIpadLandscape",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M9.72656 20.2832L19.8633 20.2832C20.166 20.2832 20.3906 20.0684 20.3906 19.7559C20.3906 19.4434 20.166 19.2285 19.8633 19.2285L9.72656 19.2285C9.41406 19.2285 9.19922 19.4434 9.19922 19.7559C9.19922 20.0684 9.41406 20.2832 9.72656 20.2832ZM3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.709 29.5898 19.2188L29.5898 3.78906C29.5898 1.29883 28.3105 0.0292969 25.7812 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.83789 21.25C2.4707 21.25 1.72852 20.5273 1.72852 19.1309L1.72852 3.87695C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L25.752 1.75781C27.0898 1.75781 27.8516 2.48047 27.8516 3.87695L27.8516 19.1309C27.8516 20.5273 27.0898 21.25 25.752 21.25Z", fillAlpha = 0.85f)
        }
        return _sFIpadLandscape!!
    }

private var _sFIpadLandscape: ImageVector? = null
