package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDotSquareshapeFill (dualtone)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Dualtone.SFDotSquareshapeFill: ImageVector
    get() {
        if (_sFDotSquareshapeFill != null) {
            return _sFDotSquareshapeFill!!
        }
        _sFDotSquareshapeFill = sfIcon(
            name = "Dualtone.SFDotSquareshapeFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 22.0703C0 22.627 0.361328 22.9785 0.908203 22.9785L22.0508 22.9785C22.5977 22.9785 22.959 22.627 22.959 22.0703L22.959 0.9375C22.959 0.380859 22.5977 0.0292969 22.0508 0.0292969L0.908203 0.0292969C0.361328 0.0292969 0 0.380859 0 0.9375Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 15.5371C9.24805 15.5371 7.45117 13.7402 7.45117 11.5039C7.45117 9.26758 9.24805 7.46094 11.4844 7.46094C13.7207 7.46094 15.5273 9.26758 15.5273 11.5039C15.5273 13.7402 13.7207 15.5371 11.4844 15.5371Z", fillAlpha = 0.85f)
        }
        return _sFDotSquareshapeFill!!
    }

private var _sFDotSquareshapeFill: ImageVector? = null
