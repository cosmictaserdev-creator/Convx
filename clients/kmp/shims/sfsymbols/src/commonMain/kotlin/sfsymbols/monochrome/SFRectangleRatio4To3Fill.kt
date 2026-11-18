package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio4To3Fill (monochrome)
 * Viewport: 23.3203 x 17.3535
 */
public val SfSymbols.Monochrome.SFRectangleRatio4To3Fill: ImageVector
    get() {
        if (_sFRectangleRatio4To3Fill != null) {
            return _sFRectangleRatio4To3Fill!!
        }
        _sFRectangleRatio4To3Fill = sfIcon(
            name = "Monochrome.SFRectangleRatio4To3Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 17.3535f
        ) {
            addSfPath("M0 3.81836L0 13.5449C0 16.0742 1.2793 17.3535 3.75977 17.3535L19.1895 17.3535C21.6797 17.3535 22.959 16.0742 22.959 13.5449L22.959 3.81836C22.959 1.29883 21.6895 0.0195312 19.1895 0.0195312L3.75977 0.0195312C1.25977 0.0195312 0 1.29883 0 3.81836Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio4To3Fill!!
    }

private var _sFRectangleRatio4To3Fill: ImageVector? = null
