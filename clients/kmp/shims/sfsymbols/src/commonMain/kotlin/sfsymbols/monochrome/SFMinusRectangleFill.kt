package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFMinusRectangleFill: ImageVector
    get() {
        if (_sFMinusRectangleFill != null) {
            return _sFMinusRectangleFill!!
        }
        _sFMinusRectangleFill = sfIcon(
            name = "Monochrome.SFMinusRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM9.52148 10.5664C8.88672 10.5664 8.47656 10.918 8.47656 11.5137C8.47656 12.0898 8.91602 12.4316 9.52148 12.4316L20.0879 12.4316C20.6934 12.4316 21.123 12.0898 21.123 11.5137C21.123 10.918 20.7227 10.5664 20.0879 10.5664Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectangleFill!!
    }

private var _sFMinusRectangleFill: ImageVector? = null
