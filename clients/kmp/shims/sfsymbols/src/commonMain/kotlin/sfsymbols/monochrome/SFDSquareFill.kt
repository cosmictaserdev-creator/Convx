package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFDSquareFill: ImageVector
    get() {
        if (_sFDSquareFill != null) {
            return _sFDSquareFill!!
        }
        _sFDSquareFill = sfIcon(
            name = "Monochrome.SFDSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.03711 5.54688C7.4707 5.54688 7.16797 5.94727 7.16797 6.54297L7.16797 16.2207C7.16797 16.8262 7.4707 17.2266 8.03711 17.2266L11.3672 17.2266C14.9316 17.2266 16.9922 15.1367 16.9922 11.3477C16.9922 7.64648 14.9609 5.54688 11.3672 5.54688ZM15.1855 11.377C15.1855 14.3262 13.7305 15.8203 11.1621 15.8203L8.90625 15.8203L8.90625 6.94336L11.1621 6.94336C13.7207 6.94336 15.1855 8.52539 15.1855 11.377Z", fillAlpha = 0.85f)
        }
        return _sFDSquareFill!!
    }

private var _sFDSquareFill: ImageVector? = null
