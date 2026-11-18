package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeForwardFill (monochrome)
 * Viewport: 27.5098 x 22.8223
 */
public val SfSymbols.Monochrome.SFArrowshapeForwardFill: ImageVector
    get() {
        if (_sFArrowshapeForwardFill != null) {
            return _sFArrowshapeForwardFill!!
        }
        _sFArrowshapeForwardFill = sfIcon(
            name = "Monochrome.SFArrowshapeForwardFill",
            viewportWidth = 27.5098f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M3.44727 6.5918C1.67969 6.5918 0.673828 7.56836 0.673828 9.28711L0.673828 13.5547C0.673828 15.2832 1.67969 16.25 3.44727 16.25L18.9551 16.25C20.7227 16.25 21.7285 15.2832 21.7285 13.5547L21.7285 9.28711C21.7285 7.56836 20.7227 6.5918 18.9551 6.5918ZM14.043 21.4648C14.043 22.2363 14.5898 22.8027 15.3711 22.8027C15.9277 22.8027 16.3867 22.5684 16.9141 22.0703L26.8457 12.7344C27.3633 12.2461 27.5098 11.7969 27.5098 11.4062C27.5098 10.9961 27.3633 10.5566 26.8457 10.0586L16.9141 0.791016C16.3379 0.253906 15.9473 0 15.3906 0C14.5898 0 14.043 0.634766 14.043 1.40625Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeForwardFill!!
    }

private var _sFArrowshapeForwardFill: ImageVector? = null
