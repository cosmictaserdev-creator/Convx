package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFScribble (monochrome)
 * Viewport: 25.7179 x 23.1011
 */
public val SfSymbols.Monochrome.SFScribble: ImageVector
    get() {
        if (_sFScribble != null) {
            return _sFScribble!!
        }
        _sFScribble = sfIcon(
            name = "Monochrome.SFScribble",
            viewportWidth = 25.7179f,
            viewportHeight = 23.1011f
        ) {
            addSfPath("M0.241755 14.9734C0.56402 15.3152 1.06207 15.3054 1.3941 14.9734L3.6109 12.7566C11.9312 4.43629 15.9253 0.510509 17.4878 2.06324C18.6207 3.19606 16.1011 6.17457 12.5855 10.3347C9.22613 14.2898 5.54449 18.3816 8.00543 20.8621C10.0562 22.9324 13.4351 20.491 16.9214 17.0632C19.5875 14.4265 21.5992 12.5906 22.3121 13.2937C22.898 13.8796 22.1558 15.0027 21.1304 16.7898C19.9195 18.8894 18.4546 21.0476 19.8316 22.4343C20.8961 23.489 22.7418 22.8738 25.0367 20.491C25.4468 20.0808 25.4566 19.5925 25.1148 19.2605C24.7828 18.9285 24.3043 18.9382 23.9527 19.2703C22.1949 20.9304 21.2281 21.5066 20.8765 21.1453C20.4761 20.7546 20.9937 19.866 22.3511 17.6394C24.0601 14.8367 24.5972 13.2742 23.4937 12.1707C22.0093 10.6863 19.773 11.9949 15.73 15.8816C11.9605 19.4949 10.0757 20.6082 9.15777 19.7097C8.05425 18.6257 9.5191 16.3113 13.8062 11.3894C18.855 5.55934 20.564 2.83473 18.6304 0.9109C16.1988-1.52074 13.5132 0.549572 2.45855 11.6043L0.241755 13.8211C-0.0902766 14.1531-0.0707453 14.6414 0.241755 14.9734Z", fillAlpha = 0.85f)
        }
        return _sFScribble!!
    }

private var _sFScribble: ImageVector? = null
