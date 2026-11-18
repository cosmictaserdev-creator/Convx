package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPyramidFill (monochrome)
 * Viewport: 26.1993 x 28.6621
 */
public val SfSymbols.Monochrome.SFPyramidFill: ImageVector
    get() {
        if (_sFPyramidFill != null) {
            return _sFPyramidFill!!
        }
        _sFPyramidFill = sfIcon(
            name = "Monochrome.SFPyramidFill",
            viewportWidth = 26.1993f,
            viewportHeight = 28.6621f
        ) {
            addSfPath("M0.252982 21.7969C-0.2353 22.6465-0.000924527 23.4375 0.819388 23.7988L11.1514 28.2715C11.5323 28.4375 11.8643 28.5645 12.1963 28.6426L12.1963 1.25C12.0499 1.34766 11.8936 1.49414 11.7862 1.68945ZM25.585 21.8164L14.042 1.70898C13.9249 1.51367 13.7881 1.36719 13.6319 1.26953L13.6319 28.6621C13.9737 28.5742 14.3155 28.4375 14.7159 28.2715L25.0186 23.8184C25.8389 23.457 26.0733 22.666 25.585 21.8164Z", fillAlpha = 0.85f)
        }
        return _sFPyramidFill!!
    }

private var _sFPyramidFill: ImageVector? = null
