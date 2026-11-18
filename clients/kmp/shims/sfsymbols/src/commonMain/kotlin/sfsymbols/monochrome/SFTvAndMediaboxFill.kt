package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvAndMediaboxFill (monochrome)
 * Viewport: 37.8125 x 30.752
 */
public val SfSymbols.Monochrome.SFTvAndMediaboxFill: ImageVector
    get() {
        if (_sFTvAndMediaboxFill != null) {
            return _sFTvAndMediaboxFill!!
        }
        _sFTvAndMediaboxFill = sfIcon(
            name = "Monochrome.SFTvAndMediaboxFill",
            viewportWidth = 37.8125f,
            viewportHeight = 30.752f
        ) {
            addSfPath("M16.0254 26.6699C16.0254 27.0262 16.0548 27.3585 16.112 27.666L10.4395 27.666C9.96094 27.666 9.57031 27.2754 9.57031 26.7969C9.57031 26.3086 9.96094 25.918 10.4395 25.918L16.0254 25.918ZM32.1875 6.11328L32.1875 18.0664L20.0977 18.0664C17.7246 18.0664 16.0254 19.4531 16.0254 22.041L16.0254 23.6621L4.24805 23.6621C2.23633 23.6621 1.19141 22.666 1.19141 20.6152L1.19141 6.11328C1.19141 4.0625 2.23633 3.06641 4.24805 3.06641L29.1309 3.06641C31.1426 3.06641 32.1875 4.0625 32.1875 6.11328Z", fillAlpha = 0.85f)
            addSfPath("M20.0977 29.1992L33.75 29.1992C35.4102 29.1992 36.2598 28.3496 36.2598 26.6699L36.2598 22.1582C36.2598 20.4688 35.4102 19.6289 33.75 19.6289L20.0977 19.6289C18.418 19.6289 17.5879 20.4688 17.5879 22.041L17.5879 26.6699C17.5879 28.3496 18.418 29.1992 20.0977 29.1992ZM32.2363 25C31.4648 25 30.8398 24.3848 30.8398 23.6133C30.8398 22.8613 31.4648 22.2461 32.2363 22.2461C32.9785 22.2461 33.6035 22.8613 33.6035 23.6133C33.6035 24.3848 32.9785 25 32.2363 25Z", fillAlpha = 0.85f)
        }
        return _sFTvAndMediaboxFill!!
    }

private var _sFTvAndMediaboxFill: ImageVector? = null
