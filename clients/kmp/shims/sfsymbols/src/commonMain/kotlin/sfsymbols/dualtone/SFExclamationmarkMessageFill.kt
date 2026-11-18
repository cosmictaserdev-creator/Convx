package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkMessageFill (dualtone)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Dualtone.SFExclamationmarkMessageFill: ImageVector
    get() {
        if (_sFExclamationmarkMessageFill != null) {
            return _sFExclamationmarkMessageFill!!
        }
        _sFExclamationmarkMessageFill = sfIcon(
            name = "Dualtone.SFExclamationmarkMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M14.3262 23.8086C22.6465 23.8086 28.6621 18.7891 28.6621 11.9043C28.6621 4.99023 22.6367 0 14.3262 0C6.01562 0 0 4.99023 0 11.9043C0 16.8848 2.75391 18.2129 2.75391 20.4785C2.75391 21.4746 2.41211 22.1191 1.66016 22.793C1.17188 23.2324 1.41602 23.8086 2.13867 23.8086C3.82812 23.8086 5.60547 23.2031 6.875 22.2559C9.02344 23.2715 11.5625 23.8086 14.3262 23.8086Z", fillAlpha = 0.2125f)
            addSfPath("M14.4531 14.3359C13.8867 14.3359 13.5645 14.0039 13.5547 13.418L13.4082 6.54297C13.3984 5.95703 13.8184 5.53711 14.4434 5.53711C15.0488 5.53711 15.4883 5.9668 15.4785 6.55273L15.3223 13.418C15.3125 14.0137 14.9902 14.3359 14.4531 14.3359ZM14.4531 18.4082C13.7598 18.4082 13.1738 17.8516 13.1738 17.1777C13.1738 16.4941 13.75 15.9375 14.4531 15.9375C15.1465 15.9375 15.7227 16.4844 15.7227 17.1777C15.7227 17.8613 15.1367 18.4082 14.4531 18.4082Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkMessageFill!!
    }

private var _sFExclamationmarkMessageFill: ImageVector? = null
