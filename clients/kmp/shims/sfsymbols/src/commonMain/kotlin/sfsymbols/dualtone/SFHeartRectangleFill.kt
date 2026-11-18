package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFHeartRectangleFill: ImageVector
    get() {
        if (_sFHeartRectangleFill != null) {
            return _sFHeartRectangleFill!!
        }
        _sFHeartRectangleFill = sfIcon(
            name = "Dualtone.SFHeartRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.6895 5.75195C13.0762 5.75195 14.1602 6.5625 14.7949 7.70508C15.4297 6.5625 16.5332 5.75195 17.9004 5.75195C20.0781 5.75195 21.6504 7.40234 21.6504 9.6582C21.6504 13.125 17.8906 16.2109 15.3906 17.8809C15.2051 18.0176 14.9707 18.1641 14.8145 18.1641C14.668 18.1641 14.4141 18.0176 14.1992 17.8809C11.6602 16.2793 7.93945 13.125 7.93945 9.6582C7.93945 7.40234 9.52148 5.75195 11.6895 5.75195Z", fillAlpha = 0.85f)
        }
        return _sFHeartRectangleFill!!
    }

private var _sFHeartRectangleFill: ImageVector? = null
