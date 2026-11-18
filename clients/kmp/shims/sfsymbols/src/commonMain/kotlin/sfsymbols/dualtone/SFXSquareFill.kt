package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFXSquareFill: ImageVector
    get() {
        if (_sFXSquareFill != null) {
            return _sFXSquareFill!!
        }
        _sFXSquareFill = sfIcon(
            name = "Dualtone.SFXSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.68555 17.3633C7.2168 17.3633 6.85547 17.0312 6.85547 16.5918C6.85547 16.3574 6.91406 16.2207 7.06055 16.0059L10.3809 11.3281L7.2168 6.86523C7.04102 6.63086 6.97266 6.47461 6.97266 6.23047C6.97266 5.77148 7.37305 5.40039 7.8418 5.40039C8.22266 5.40039 8.4375 5.53711 8.69141 5.88867L11.5527 10.0879L11.6113 10.0879L14.4336 5.89844C14.668 5.53711 14.8926 5.40039 15.2441 5.40039C15.7227 5.40039 16.0938 5.74219 16.0938 6.19141C16.0938 6.38672 16.0254 6.57227 15.8691 6.77734L12.5586 11.377L15.8398 15.957C15.9863 16.1523 16.0547 16.3184 16.0547 16.5527C16.0547 17.0312 15.6836 17.3633 15.1855 17.3633C14.8242 17.3633 14.6289 17.2266 14.3457 16.8457L11.4648 12.666L11.416 12.666L8.51562 16.8457C8.23242 17.2266 8.04688 17.3633 7.68555 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFXSquareFill!!
    }

private var _sFXSquareFill: ImageVector? = null
