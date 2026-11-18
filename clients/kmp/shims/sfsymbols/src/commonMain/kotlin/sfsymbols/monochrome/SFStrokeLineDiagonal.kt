package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStrokeLineDiagonal (monochrome)
 * Viewport: 20.9888 x 20.6446
 */
public val SfSymbols.Monochrome.SFStrokeLineDiagonal: ImageVector
    get() {
        if (_sFStrokeLineDiagonal != null) {
            return _sFStrokeLineDiagonal!!
        }
        _sFStrokeLineDiagonal = sfIcon(
            name = "Monochrome.SFStrokeLineDiagonal",
            viewportWidth = 20.9888f,
            viewportHeight = 20.6446f
        ) {
            addSfPath("M0.440682 18.0909C-0.145256 18.667-0.145256 19.6143 0.430916 20.2002C1.00709 20.7862 1.98365 20.7959 2.55982 20.21L20.1965 2.58304C20.7727 2.00687 20.7629 1.04007 20.1965 0.463898C19.6204-0.12204 18.6438-0.12204 18.0676 0.454132Z", fillAlpha = 0.85f)
        }
        return _sFStrokeLineDiagonal!!
    }

private var _sFStrokeLineDiagonal: ImageVector? = null
