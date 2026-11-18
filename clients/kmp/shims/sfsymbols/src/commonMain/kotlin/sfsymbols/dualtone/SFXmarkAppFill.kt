package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFXmarkAppFill: ImageVector
    get() {
        if (_sFXmarkAppFill != null) {
            return _sFXmarkAppFill!!
        }
        _sFXmarkAppFill = sfIcon(
            name = "Dualtone.SFXmarkAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M7.63672 16.543L16.543 7.63672C16.7188 7.46094 16.8164 7.23633 16.8164 6.98242C16.8164 6.49414 16.416 6.11328 15.918 6.11328C15.6641 6.11328 15.459 6.20117 15.2832 6.37695L6.34766 15.2734C6.17188 15.459 6.08398 15.6738 6.08398 15.918C6.08398 16.416 6.48438 16.8164 6.99219 16.8164C7.24609 16.8164 7.46094 16.7188 7.63672 16.543ZM15.2539 16.543C15.4395 16.7188 15.6543 16.8164 15.918 16.8164C16.416 16.8164 16.8164 16.416 16.8164 15.918C16.8164 15.6738 16.7188 15.459 16.543 15.2734L7.61719 6.37695C7.44141 6.20117 7.22656 6.11328 6.99219 6.11328C6.48438 6.11328 6.08398 6.49414 6.08398 6.98242C6.08398 7.23633 6.17188 7.46094 6.34766 7.63672Z", fillAlpha = 0.85f)
        }
        return _sFXmarkAppFill!!
    }

private var _sFXmarkAppFill: ImageVector? = null
