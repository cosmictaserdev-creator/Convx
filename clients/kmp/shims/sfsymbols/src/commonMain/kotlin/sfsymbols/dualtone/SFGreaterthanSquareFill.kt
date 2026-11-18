package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthanSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFGreaterthanSquareFill: ImageVector
    get() {
        if (_sFGreaterthanSquareFill != null) {
            return _sFGreaterthanSquareFill!!
        }
        _sFGreaterthanSquareFill = sfIcon(
            name = "Dualtone.SFGreaterthanSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.20312 16.6016C7.66602 16.6016 7.27539 16.2109 7.27539 15.7031C7.27539 15.2832 7.4707 15.0195 7.92969 14.8047L14.5312 11.4844L14.5312 11.4062L7.92969 8.03711C7.48047 7.8125 7.27539 7.53906 7.27539 7.11914C7.27539 6.62109 7.65625 6.24023 8.18359 6.24023C8.42773 6.24023 8.56445 6.28906 8.75 6.37695L16.3379 10.4883C16.7676 10.7324 16.9922 11.0547 16.9922 11.4746C16.9922 11.9434 16.7871 12.2266 16.3379 12.4609L8.75 16.4551C8.57422 16.543 8.42773 16.6016 8.20312 16.6016Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthanSquareFill!!
    }

private var _sFGreaterthanSquareFill: ImageVector? = null
