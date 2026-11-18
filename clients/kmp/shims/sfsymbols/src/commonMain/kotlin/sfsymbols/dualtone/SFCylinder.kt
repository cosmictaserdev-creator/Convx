package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCylinder (dualtone)
 * Viewport: 21.6309 x 26.084
 */
public val SfSymbols.Dualtone.SFCylinder: ImageVector
    get() {
        if (_sFCylinder != null) {
            return _sFCylinder!!
        }
        _sFCylinder = sfIcon(
            name = "Dualtone.SFCylinder",
            viewportWidth = 21.6309f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M10.6348 26.0645C17.0117 26.0645 21.2695 23.6523 21.2695 20.4395L21.2695 5.08789L19.5312 5.08789L19.5312 20.4395C19.5312 22.6758 15.9766 24.4336 10.6348 24.4336C5.29297 24.4336 1.72852 22.6758 1.72852 20.4395L1.72852 5.08789L0 5.08789L0 20.4395C0 23.6523 4.25781 26.0645 10.6348 26.0645ZM10.6348 10.1758C17.0117 10.1758 21.2695 8.06641 21.2695 5.08789C21.2695 2.10938 17.0117 0 10.6348 0C4.25781 0 0 2.10938 0 5.08789C0 8.06641 4.25781 10.1758 10.6348 10.1758ZM10.6348 8.54492C5.29297 8.54492 1.72852 7.08984 1.72852 5.08789C1.72852 3.02734 5.29297 1.5332 10.6348 1.5332C15.9766 1.5332 19.5312 3.02734 19.5312 5.08789C19.5312 7.08984 15.9766 8.54492 10.6348 8.54492Z", fillAlpha = 0.85f)
        }
        return _sFCylinder!!
    }

private var _sFCylinder: ImageVector? = null
