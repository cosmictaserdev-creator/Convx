package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFNSquareFill: ImageVector
    get() {
        if (_sFNSquareFill != null) {
            return _sFNSquareFill!!
        }
        _sFNSquareFill = sfIcon(
            name = "Dualtone.SFNSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.59766 17.3633C7.07031 17.3633 6.75781 17.0215 6.75781 16.4746L6.75781 6.38672C6.75781 5.77148 7.08984 5.40039 7.66602 5.40039C8.05664 5.40039 8.32031 5.54688 8.61328 5.97656L14.4434 14.4531L14.5312 14.4531L14.5312 6.29883C14.5312 5.75195 14.834 5.40039 15.3516 5.40039C15.8887 5.40039 16.2012 5.73242 16.2012 6.29883L16.2012 16.4062C16.2012 17.0117 15.8496 17.3633 15.2832 17.3633C14.8926 17.3633 14.6582 17.2266 14.3555 16.7969L8.51562 8.4082L8.4375 8.4082L8.4375 16.4746C8.4375 17.041 8.11523 17.3633 7.59766 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFNSquareFill!!
    }

private var _sFNSquareFill: ImageVector? = null
