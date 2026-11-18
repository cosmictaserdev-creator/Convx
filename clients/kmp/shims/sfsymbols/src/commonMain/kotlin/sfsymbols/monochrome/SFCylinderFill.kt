package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCylinderFill (monochrome)
 * Viewport: 21.6309 x 26.084
 */
public val SfSymbols.Monochrome.SFCylinderFill: ImageVector
    get() {
        if (_sFCylinderFill != null) {
            return _sFCylinderFill!!
        }
        _sFCylinderFill = sfIcon(
            name = "Monochrome.SFCylinderFill",
            viewportWidth = 21.6309f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M0 20.4395C0 23.6523 4.31641 26.0645 10.6348 26.0645C16.9531 26.0645 21.2695 23.6523 21.2695 20.4395L21.2695 5.08789C21.2695 2.10938 17.0117 0 10.6348 0C4.25781 0 0 2.10938 0 5.08789ZM1.72852 5.08789C1.72852 3.02734 5.29297 1.5332 10.6348 1.5332C15.9766 1.5332 19.5312 3.02734 19.5312 5.08789C19.5312 7.08984 15.9082 8.54492 10.6348 8.54492C5.35156 8.54492 1.72852 7.08984 1.72852 5.08789Z", fillAlpha = 0.85f)
        }
        return _sFCylinderFill!!
    }

private var _sFCylinderFill: ImageVector? = null
