package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFNSquareFill: ImageVector
    get() {
        if (_sFNSquareFill != null) {
            return _sFNSquareFill!!
        }
        _sFNSquareFill = sfIcon(
            name = "Monochrome.SFNSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.5312 6.29883L14.5312 14.4531L14.4434 14.4531L8.61328 5.97656C8.32031 5.54688 8.05664 5.40039 7.66602 5.40039C7.08984 5.40039 6.75781 5.77148 6.75781 6.38672L6.75781 16.4746C6.75781 17.0215 7.07031 17.3633 7.59766 17.3633C8.11523 17.3633 8.4375 17.041 8.4375 16.4746L8.4375 8.4082L8.51562 8.4082L14.3555 16.7969C14.6582 17.2266 14.8926 17.3633 15.2832 17.3633C15.8496 17.3633 16.2012 17.0117 16.2012 16.4062L16.2012 6.29883C16.2012 5.73242 15.8887 5.40039 15.3516 5.40039C14.834 5.40039 14.5312 5.75195 14.5312 6.29883Z", fillAlpha = 0.85f)
        }
        return _sFNSquareFill!!
    }

private var _sFNSquareFill: ImageVector? = null
