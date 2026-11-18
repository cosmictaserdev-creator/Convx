package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCylinderSplit1x2Fill (dualtone)
 * Viewport: 21.6309 x 26.084
 */
public val SfSymbols.Dualtone.SFCylinderSplit1x2Fill: ImageVector
    get() {
        if (_sFCylinderSplit1x2Fill != null) {
            return _sFCylinderSplit1x2Fill!!
        }
        _sFCylinderSplit1x2Fill = sfIcon(
            name = "Dualtone.SFCylinderSplit1x2Fill",
            viewportWidth = 21.6309f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M0 15.0684L0 13.1836C2.57812 15.3613 6.09375 16.4062 10.6348 16.4062C15.166 16.4062 18.6816 15.3613 21.2695 13.1836L21.2695 15.0684C18.6133 17.0801 15.1172 18.0371 10.6348 18.0371C6.15234 18.0371 2.64648 17.0801 0 15.0684ZM0 20.4395C0 23.6523 4.31641 26.0645 10.6348 26.0645C16.9531 26.0645 21.2695 23.6523 21.2695 20.4395L21.2695 5.08789C21.2695 2.10938 17.0117 0 10.6348 0C4.25781 0 0 2.10938 0 5.08789ZM1.72852 5.08789C1.72852 3.02734 5.29297 1.5332 10.6348 1.5332C15.9766 1.5332 19.5312 3.02734 19.5312 5.08789C19.5312 7.08984 15.9082 8.54492 10.6348 8.54492C5.35156 8.54492 1.72852 7.08984 1.72852 5.08789Z", fillAlpha = 0.85f)
        }
        return _sFCylinderSplit1x2Fill!!
    }

private var _sFCylinderSplit1x2Fill: ImageVector? = null
