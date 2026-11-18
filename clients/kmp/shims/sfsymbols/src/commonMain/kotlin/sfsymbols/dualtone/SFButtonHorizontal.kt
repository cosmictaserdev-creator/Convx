package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonHorizontal (dualtone)
 * Viewport: 29.6387 x 22.2949
 */
public val SfSymbols.Dualtone.SFButtonHorizontal: ImageVector
    get() {
        if (_sFButtonHorizontal != null) {
            return _sFButtonHorizontal!!
        }
        _sFButtonHorizontal = sfIcon(
            name = "Dualtone.SFButtonHorizontal",
            viewportWidth = 29.6387f,
            viewportHeight = 22.2949f
        ) {
            addSfPath("M9.59961 22.2754L19.6777 22.2754C26.8164 22.2754 29.2773 19.6094 29.2773 13.0371L29.2773 9.23828C29.2773 2.66602 26.8164 0 19.6777 0L9.59961 0C2.46094 0 0 2.66602 0 9.23828L0 13.0371C0 19.6094 2.46094 22.2754 9.59961 22.2754ZM9.59961 20.5469C3.61328 20.5469 1.72852 18.5352 1.72852 13.0371L1.72852 9.23828C1.72852 3.74023 3.61328 1.73828 9.59961 1.73828L19.6777 1.73828C25.6641 1.73828 27.5488 3.74023 27.5488 9.23828L27.5488 13.0371C27.5488 18.5352 25.6641 20.5469 19.6777 20.5469Z", fillAlpha = 0.85f)
        }
        return _sFButtonHorizontal!!
    }

private var _sFButtonHorizontal: ImageVector? = null
