package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonHorizontalFill (dualtone)
 * Viewport: 29.6387 x 22.2949
 */
public val SfSymbols.Dualtone.SFButtonHorizontalFill: ImageVector
    get() {
        if (_sFButtonHorizontalFill != null) {
            return _sFButtonHorizontalFill!!
        }
        _sFButtonHorizontalFill = sfIcon(
            name = "Dualtone.SFButtonHorizontalFill",
            viewportWidth = 29.6387f,
            viewportHeight = 22.2949f
        ) {
            addSfPath("M9.59961 22.2754L19.6777 22.2754C26.8164 22.2754 29.2773 19.6094 29.2773 13.0371L29.2773 9.23828C29.2773 2.66602 26.8164 0 19.6777 0L9.59961 0C2.46094 0 0 2.66602 0 9.23828L0 13.0371C0 19.6094 2.46094 22.2754 9.59961 22.2754Z", fillAlpha = 0.85f)
        }
        return _sFButtonHorizontalFill!!
    }

private var _sFButtonHorizontalFill: ImageVector? = null
