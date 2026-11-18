package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFMinusRectangleFill: ImageVector
    get() {
        if (_sFMinusRectangleFill != null) {
            return _sFMinusRectangleFill!!
        }
        _sFMinusRectangleFill = sfIcon(
            name = "Dualtone.SFMinusRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M9.52148 12.4316C8.91602 12.4316 8.47656 12.0898 8.47656 11.5137C8.47656 10.918 8.88672 10.5664 9.52148 10.5664L20.0879 10.5664C20.7227 10.5664 21.123 10.918 21.123 11.5137C21.123 12.0898 20.6934 12.4316 20.0879 12.4316Z", fillAlpha = 0.85f)
        }
        return _sFMinusRectangleFill!!
    }

private var _sFMinusRectangleFill: ImageVector? = null
