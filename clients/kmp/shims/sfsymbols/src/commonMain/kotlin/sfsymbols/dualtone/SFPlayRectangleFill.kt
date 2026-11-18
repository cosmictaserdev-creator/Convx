package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFPlayRectangleFill: ImageVector
    get() {
        if (_sFPlayRectangleFill != null) {
            return _sFPlayRectangleFill!!
        }
        _sFPlayRectangleFill = sfIcon(
            name = "Dualtone.SFPlayRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M12.3926 16.5332C11.8262 16.875 11.1816 16.5918 11.1816 16.0156L11.1816 6.96289C11.1816 6.38672 11.8652 6.14258 12.3926 6.45508L19.7559 10.8398C20.2637 11.1523 20.2734 11.8555 19.7559 12.1582Z", fillAlpha = 0.85f)
        }
        return _sFPlayRectangleFill!!
    }

private var _sFPlayRectangleFill: ImageVector? = null
