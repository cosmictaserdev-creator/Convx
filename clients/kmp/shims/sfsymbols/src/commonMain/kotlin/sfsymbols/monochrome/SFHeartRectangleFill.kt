package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFHeartRectangleFill: ImageVector
    get() {
        if (_sFHeartRectangleFill != null) {
            return _sFHeartRectangleFill!!
        }
        _sFHeartRectangleFill = sfIcon(
            name = "Monochrome.SFHeartRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM14.7949 7.70508C14.1602 6.5625 13.0762 5.75195 11.6895 5.75195C9.52148 5.75195 7.93945 7.40234 7.93945 9.6582C7.93945 13.125 11.6602 16.2793 14.1992 17.8809C14.4141 18.0176 14.668 18.1641 14.8145 18.1641C14.9707 18.1641 15.2051 18.0176 15.3906 17.8809C17.8906 16.2109 21.6504 13.125 21.6504 9.6582C21.6504 7.40234 20.0781 5.75195 17.9004 5.75195C16.5332 5.75195 15.4297 6.5625 14.7949 7.70508Z", fillAlpha = 0.85f)
        }
        return _sFHeartRectangleFill!!
    }

private var _sFHeartRectangleFill: ImageVector? = null
