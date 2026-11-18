package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLessthanSquareFill: ImageVector
    get() {
        if (_sFLessthanSquareFill != null) {
            return _sFLessthanSquareFill!!
        }
        _sFLessthanSquareFill = sfIcon(
            name = "Monochrome.SFLessthanSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.2285 6.37695L6.65039 10.4883C6.21094 10.7324 5.99609 11.0547 5.99609 11.4746C5.99609 11.9434 6.19141 12.2266 6.65039 12.4609L14.2285 16.4551C14.4043 16.543 14.5508 16.6016 14.7754 16.6016C15.3125 16.6016 15.7031 16.2109 15.7031 15.7031C15.7031 15.2832 15.5176 15.0195 15.0586 14.8047L8.44727 11.4844L8.44727 11.4062L15.0586 8.03711C15.5078 7.8125 15.7031 7.53906 15.7031 7.11914C15.7031 6.62109 15.3223 6.24023 14.8047 6.24023C14.5508 6.24023 14.4141 6.28906 14.2285 6.37695Z", fillAlpha = 0.85f)
        }
        return _sFLessthanSquareFill!!
    }

private var _sFLessthanSquareFill: ImageVector? = null
