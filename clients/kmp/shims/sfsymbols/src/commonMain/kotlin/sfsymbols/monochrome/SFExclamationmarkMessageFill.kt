package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkMessageFill (monochrome)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Monochrome.SFExclamationmarkMessageFill: ImageVector
    get() {
        if (_sFExclamationmarkMessageFill != null) {
            return _sFExclamationmarkMessageFill!!
        }
        _sFExclamationmarkMessageFill = sfIcon(
            name = "Monochrome.SFExclamationmarkMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M28.6621 11.9043C28.6621 18.7891 22.6465 23.8086 14.3262 23.8086C11.5625 23.8086 9.02344 23.2715 6.875 22.2559C5.60547 23.2031 3.82812 23.8086 2.13867 23.8086C1.41602 23.8086 1.17188 23.2324 1.66016 22.793C2.41211 22.1191 2.75391 21.4746 2.75391 20.4785C2.75391 18.2129 0 16.8848 0 11.9043C0 4.99023 6.01562 0 14.3262 0C22.6367 0 28.6621 4.99023 28.6621 11.9043ZM13.1738 17.1777C13.1738 17.8516 13.7598 18.4082 14.4531 18.4082C15.1367 18.4082 15.7227 17.8613 15.7227 17.1777C15.7227 16.4844 15.1465 15.9375 14.4531 15.9375C13.75 15.9375 13.1738 16.4941 13.1738 17.1777ZM13.4082 6.54297L13.5547 13.418C13.5645 14.0039 13.8867 14.3359 14.4531 14.3359C14.9902 14.3359 15.3125 14.0137 15.3223 13.418L15.4785 6.55273C15.4883 5.9668 15.0488 5.53711 14.4434 5.53711C13.8184 5.53711 13.3984 5.95703 13.4082 6.54297Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkMessageFill!!
    }

private var _sFExclamationmarkMessageFill: ImageVector? = null
