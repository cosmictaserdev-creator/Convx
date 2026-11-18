package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPlayRectangleFill: ImageVector
    get() {
        if (_sFPlayRectangleFill != null) {
            return _sFPlayRectangleFill!!
        }
        _sFPlayRectangleFill = sfIcon(
            name = "Monochrome.SFPlayRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM11.1816 6.96289L11.1816 16.0156C11.1816 16.5918 11.8262 16.875 12.3926 16.5332L19.7559 12.1582C20.2734 11.8555 20.2637 11.1523 19.7559 10.8398L12.3926 6.45508C11.8652 6.14258 11.1816 6.38672 11.1816 6.96289Z", fillAlpha = 0.85f)
        }
        return _sFPlayRectangleFill!!
    }

private var _sFPlayRectangleFill: ImageVector? = null
