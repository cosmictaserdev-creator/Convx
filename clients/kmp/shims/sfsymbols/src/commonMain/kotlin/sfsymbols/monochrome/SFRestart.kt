package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRestart (monochrome)
 * Viewport: 22.041 x 21.2402
 */
public val SfSymbols.Monochrome.SFRestart: ImageVector
    get() {
        if (_sFRestart != null) {
            return _sFRestart!!
        }
        _sFRestart = sfIcon(
            name = "Monochrome.SFRestart",
            viewportWidth = 22.041f,
            viewportHeight = 21.2402f
        ) {
            addSfPath("M17.4707 21.2402C18.3105 21.2402 18.8379 20.6055 18.8379 19.6094L18.8379 1.65039C18.8379 0.644531 18.3105 0.00976562 17.4707 0.00976562C17.0215 0.00976562 16.6504 0.175781 16.1816 0.449219L1.25977 9.11133C0.332031 9.64844 0 10.0195 0 10.625C0 11.2402 0.332031 11.6113 1.25977 12.1387L16.1816 20.8105C16.6504 21.084 17.0215 21.2402 17.4707 21.2402ZM16.9531 19.2188C16.8848 19.2188 16.8262 19.1895 16.748 19.1504L2.41211 10.8301C2.32422 10.7812 2.24609 10.7227 2.24609 10.625C2.24609 10.5371 2.32422 10.4785 2.41211 10.4297L16.748 2.10938C16.8262 2.07031 16.8848 2.04102 16.9531 2.04102C17.041 2.04102 17.1094 2.09961 17.1094 2.23633L17.1094 19.0137C17.1094 19.1504 17.041 19.2188 16.9531 19.2188Z", fillAlpha = 0.85f)
        }
        return _sFRestart!!
    }

private var _sFRestart: ImageVector? = null
