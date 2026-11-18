package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeTurnUpRightFill (monochrome)
 * Viewport: 27.5098 x 22.8223
 */
public val SfSymbols.Monochrome.SFArrowshapeTurnUpRightFill: ImageVector
    get() {
        if (_sFArrowshapeTurnUpRightFill != null) {
            return _sFArrowshapeTurnUpRightFill!!
        }
        _sFArrowshapeTurnUpRightFill = sfIcon(
            name = "Monochrome.SFArrowshapeTurnUpRightFill",
            viewportWidth = 27.5098f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M17.8125 6.33789L13.6328 6.33789C4.89258 6.33789 0.673828 11.7871 0.673828 21.2988C0.673828 22.2656 1.23047 22.8027 1.85547 22.8027C2.35352 22.8027 2.86133 22.666 3.24219 21.9238C5.33203 17.832 8.51562 16.5137 13.6328 16.5137L17.8125 16.5137C20.6348 16.5137 22.9102 14.2383 22.9102 11.416C22.9102 8.60352 20.6348 6.33789 17.8125 6.33789ZM14.043 21.4648C14.043 22.2363 14.5898 22.8027 15.3711 22.8027C15.9277 22.8027 16.3867 22.5684 16.9141 22.0703L26.8457 12.7344C27.3633 12.2461 27.5098 11.7969 27.5098 11.4062C27.5098 10.9961 27.3633 10.5566 26.8457 10.0586L16.9141 0.791016C16.3379 0.253906 15.9473 0 15.3906 0C14.5898 0 14.043 0.634766 14.043 1.40625Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeTurnUpRightFill!!
    }

private var _sFArrowshapeTurnUpRightFill: ImageVector? = null
