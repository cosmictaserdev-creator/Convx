package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareshapeFill (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareshapeFill: ImageVector
    get() {
        if (_sFSquareshapeFill != null) {
            return _sFSquareshapeFill!!
        }
        _sFSquareshapeFill = sfIcon(
            name = "Monochrome.SFSquareshapeFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 22.0703C0 22.627 0.361328 22.9785 0.908203 22.9785L22.0508 22.9785C22.5977 22.9785 22.959 22.627 22.959 22.0703L22.959 0.9375C22.959 0.380859 22.5977 0.0292969 22.0508 0.0292969L0.908203 0.0292969C0.361328 0.0292969 0 0.380859 0 0.9375Z", fillAlpha = 0.85f)
        }
        return _sFSquareshapeFill!!
    }

private var _sFSquareshapeFill: ImageVector? = null
