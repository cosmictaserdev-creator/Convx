package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParagraphsign (monochrome)
 * Viewport: 20.166 x 23.6816
 */
public val SfSymbols.Monochrome.SFParagraphsign: ImageVector
    get() {
        if (_sFParagraphsign != null) {
            return _sFParagraphsign!!
        }
        _sFParagraphsign = sfIcon(
            name = "Monochrome.SFParagraphsign",
            viewportWidth = 20.166f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M0 6.49414C0 10.2539 2.66602 12.7344 6.74805 12.7344L8.83789 12.7344L8.83789 22.6953C8.83789 23.3008 9.21875 23.6816 9.81445 23.6816C10.4102 23.6816 10.8105 23.3008 10.8105 22.6953L10.8105 2.20703L13.7207 2.20703L13.7207 22.6953C13.7207 23.3008 14.0918 23.6816 14.6875 23.6816C15.2832 23.6816 15.6836 23.3008 15.6836 22.6953L15.6836 2.20703L17.8613 2.20703C18.457 2.20703 18.8477 1.80664 18.8477 1.21094C18.8477 0.615234 18.457 0.234375 17.8613 0.234375L6.69922 0.234375C2.64648 0.234375 0 2.70508 0 6.49414Z", fillAlpha = 0.85f)
        }
        return _sFParagraphsign!!
    }

private var _sFParagraphsign: ImageVector? = null
