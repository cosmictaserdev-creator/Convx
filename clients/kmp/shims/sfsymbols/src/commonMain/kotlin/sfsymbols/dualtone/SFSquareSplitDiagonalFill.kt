package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSplitDiagonalFill (dualtone)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Dualtone.SFSquareSplitDiagonalFill: ImageVector
    get() {
        if (_sFSquareSplitDiagonalFill != null) {
            return _sFSquareSplitDiagonalFill!!
        }
        _sFSquareSplitDiagonalFill = sfIcon(
            name = "Dualtone.SFSquareSplitDiagonalFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M19.1504 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 20.0586 0.146484 20.752 0.449219 21.3086L21.2793 0.488281C20.7129 0.175781 20.0098 0.0292969 19.1504 0.0292969ZM1.66992 22.5293C2.23633 22.832 2.94922 22.9785 3.79883 22.9785L19.1504 22.9785C21.6797 22.9785 22.959 21.709 22.959 19.2188L22.959 3.78906C22.959 2.95898 22.8027 2.25586 22.5 1.70898Z", fillAlpha = 0.85f)
        }
        return _sFSquareSplitDiagonalFill!!
    }

private var _sFSquareSplitDiagonalFill: ImageVector? = null
