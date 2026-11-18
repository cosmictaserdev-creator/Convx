package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSplitDiagonal2x2Fill (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareSplitDiagonal2x2Fill: ImageVector
    get() {
        if (_sFSquareSplitDiagonal2x2Fill != null) {
            return _sFSquareSplitDiagonal2x2Fill!!
        }
        _sFSquareSplitDiagonal2x2Fill = sfIcon(
            name = "Monochrome.SFSquareSplitDiagonal2x2Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M10.2539 11.5039L0.449219 1.69922C0.146484 2.25586 0 2.94922 0 3.78906L0 19.2188C0 20.0586 0.146484 20.752 0.449219 21.3086ZM11.4746 10.2832L21.2793 0.488281C20.7129 0.175781 20.0098 0.0292969 19.1504 0.0292969L3.79883 0.0292969C2.94922 0.0292969 2.23633 0.175781 1.66992 0.478516ZM12.6953 11.5039L22.5 21.2988C22.8027 20.752 22.959 20.0488 22.959 19.2188L22.959 3.78906C22.959 2.95898 22.8027 2.25586 22.5 1.70898ZM11.4746 12.7246L1.66992 22.5293C2.23633 22.832 2.94922 22.9785 3.79883 22.9785L19.1504 22.9785C20.0098 22.9785 20.7129 22.832 21.2793 22.5195Z", fillAlpha = 0.85f)
        }
        return _sFSquareSplitDiagonal2x2Fill!!
    }

private var _sFSquareSplitDiagonal2x2Fill: ImageVector? = null
