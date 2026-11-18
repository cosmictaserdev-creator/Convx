package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthanSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFGreaterthanSquareFill: ImageVector
    get() {
        if (_sFGreaterthanSquareFill != null) {
            return _sFGreaterthanSquareFill!!
        }
        _sFGreaterthanSquareFill = sfIcon(
            name = "Monochrome.SFGreaterthanSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.27539 7.11914C7.27539 7.53906 7.48047 7.8125 7.92969 8.03711L14.5312 11.4062L14.5312 11.4844L7.92969 14.8047C7.4707 15.0195 7.27539 15.2832 7.27539 15.7031C7.27539 16.2109 7.66602 16.6016 8.20312 16.6016C8.42773 16.6016 8.57422 16.543 8.75 16.4551L16.3379 12.4609C16.7871 12.2266 16.9922 11.9434 16.9922 11.4746C16.9922 11.0547 16.7676 10.7324 16.3379 10.4883L8.75 6.37695C8.56445 6.28906 8.42773 6.24023 8.18359 6.24023C7.65625 6.24023 7.27539 6.62109 7.27539 7.11914Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthanSquareFill!!
    }

private var _sFGreaterthanSquareFill: ImageVector? = null
