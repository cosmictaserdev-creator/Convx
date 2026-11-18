package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivide (monochrome)
 * Viewport: 20.918 x 18.0566
 */
public val SfSymbols.Monochrome.SFDivide: ImageVector
    get() {
        if (_sFDivide != null) {
            return _sFDivide!!
        }
        _sFDivide = sfIcon(
            name = "Monochrome.SFDivide",
            viewportWidth = 20.918f,
            viewportHeight = 18.0566f
        ) {
            addSfPath("M10.2734 3.4668C11.2109 3.4668 11.9727 2.70508 11.9727 1.78711C11.9727 0.849609 11.2109 0.0878906 10.2734 0.0878906C9.35547 0.0878906 8.58398 0.849609 8.58398 1.78711C8.58398 2.70508 9.35547 3.4668 10.2734 3.4668ZM10.2734 18.0566C11.2109 18.0566 11.9727 17.2949 11.9727 16.3672C11.9727 15.4395 11.2109 14.6777 10.2734 14.6777C9.35547 14.6777 8.58398 15.4395 8.58398 16.3672C8.58398 17.2949 9.35547 18.0566 10.2734 18.0566ZM0.957031 10L19.5996 10C20.1172 10 20.5566 9.56055 20.5566 9.04297C20.5566 8.51562 20.1172 8.08594 19.5996 8.08594L0.957031 8.08594C0.439453 8.08594 0 8.51562 0 9.04297C0 9.56055 0.439453 10 0.957031 10Z", fillAlpha = 0.85f)
        }
        return _sFDivide!!
    }

private var _sFDivide: ImageVector? = null
