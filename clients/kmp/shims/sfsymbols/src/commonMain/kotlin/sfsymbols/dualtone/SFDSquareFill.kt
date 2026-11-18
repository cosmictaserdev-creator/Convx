package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDSquareFill: ImageVector
    get() {
        if (_sFDSquareFill != null) {
            return _sFDSquareFill!!
        }
        _sFDSquareFill = sfIcon(
            name = "Dualtone.SFDSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.03711 17.2266C7.4707 17.2266 7.16797 16.8262 7.16797 16.2207L7.16797 6.54297C7.16797 5.94727 7.4707 5.54688 8.03711 5.54688L11.3672 5.54688C14.9609 5.54688 16.9922 7.64648 16.9922 11.3477C16.9922 15.1367 14.9316 17.2266 11.3672 17.2266ZM8.90625 15.8203L11.1621 15.8203C13.7305 15.8203 15.1855 14.3262 15.1855 11.377C15.1855 8.52539 13.7207 6.94336 11.1621 6.94336L8.90625 6.94336Z", fillAlpha = 0.85f)
        }
        return _sFDSquareFill!!
    }

private var _sFDSquareFill: ImageVector? = null
