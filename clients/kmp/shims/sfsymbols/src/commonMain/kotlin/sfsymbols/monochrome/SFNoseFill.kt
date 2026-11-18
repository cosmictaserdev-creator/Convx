package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNoseFill (monochrome)
 * Viewport: 25.4102 x 24.4434
 */
public val SfSymbols.Monochrome.SFNoseFill: ImageVector
    get() {
        if (_sFNoseFill != null) {
            return _sFNoseFill!!
        }
        _sFNoseFill = sfIcon(
            name = "Monochrome.SFNoseFill",
            viewportWidth = 25.4102f,
            viewportHeight = 24.4434f
        ) {
            addSfPath("M0 18.8574C0 21.9629 2.14844 24.0234 5.41992 24.0234C6.44531 24.0234 7.51953 23.6816 8.26172 23.1445C7.70508 22.7441 7.2168 22.2949 6.58203 21.5039C6.29883 21.6504 5.91797 21.7578 5.55664 21.7578C4.59961 21.7578 3.94531 21.2012 3.94531 20.4102C3.94531 19.6582 4.51172 19.1309 5.3418 19.1309C6.07422 19.1309 6.5625 19.5605 7.12891 20.1855C8.52539 21.6895 9.77539 23.1738 12.5293 23.1738C15.2734 23.1738 16.5234 21.6895 17.9199 20.1855C18.4863 19.5605 18.9746 19.1309 19.707 19.1309C20.5371 19.1309 21.1035 19.6582 21.1035 20.4102C21.1035 21.2012 20.459 21.7578 19.502 21.7578C19.1309 21.7578 18.75 21.6504 18.4766 21.5039C17.8418 22.2949 17.3535 22.7441 16.7969 23.1445C17.5293 23.6816 18.6133 24.0234 19.6387 24.0234C22.9102 24.0234 25.0488 21.9629 25.0488 18.8574C25.0488 15.8789 22.9004 13.6621 19.5996 13.3301C18.3594 10.5566 17.3438 6.86523 16.6211 3.75977C16.0547 1.30859 14.7559 0 12.5293 0C10.293 0 9.00391 1.30859 8.42773 3.75977C7.70508 6.86523 6.68945 10.5566 5.45898 13.3301C2.1582 13.6621 0 15.8789 0 18.8574Z", fillAlpha = 0.85f)
        }
        return _sFNoseFill!!
    }

private var _sFNoseFill: ImageVector? = null
