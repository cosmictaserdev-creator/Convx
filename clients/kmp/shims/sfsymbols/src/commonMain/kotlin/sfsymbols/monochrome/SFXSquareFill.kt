package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFXSquareFill: ImageVector
    get() {
        if (_sFXSquareFill != null) {
            return _sFXSquareFill!!
        }
        _sFXSquareFill = sfIcon(
            name = "Monochrome.SFXSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.4336 5.89844L11.6113 10.0879L11.5527 10.0879L8.69141 5.88867C8.4375 5.53711 8.22266 5.40039 7.8418 5.40039C7.37305 5.40039 6.97266 5.77148 6.97266 6.23047C6.97266 6.47461 7.04102 6.63086 7.2168 6.86523L10.3809 11.3281L7.06055 16.0059C6.91406 16.2207 6.85547 16.3574 6.85547 16.5918C6.85547 17.0312 7.2168 17.3633 7.68555 17.3633C8.04688 17.3633 8.23242 17.2266 8.51562 16.8457L11.416 12.666L11.4648 12.666L14.3457 16.8457C14.6289 17.2266 14.8242 17.3633 15.1855 17.3633C15.6836 17.3633 16.0547 17.0312 16.0547 16.5527C16.0547 16.3184 15.9863 16.1523 15.8398 15.957L12.5586 11.377L15.8691 6.77734C16.0254 6.57227 16.0938 6.38672 16.0938 6.19141C16.0938 5.74219 15.7227 5.40039 15.2441 5.40039C14.8926 5.40039 14.668 5.53711 14.4336 5.89844Z", fillAlpha = 0.85f)
        }
        return _sFXSquareFill!!
    }

private var _sFXSquareFill: ImageVector? = null
