package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrapezoidAndLineHorizontalFill (monochrome)
 * Viewport: 30.4102 x 21.8384
 */
public val SfSymbols.Monochrome.SFTrapezoidAndLineHorizontalFill: ImageVector
    get() {
        if (_sFTrapezoidAndLineHorizontalFill != null) {
            return _sFTrapezoidAndLineHorizontalFill!!
        }
        _sFTrapezoidAndLineHorizontalFill = sfIcon(
            name = "Monochrome.SFTrapezoidAndLineHorizontalFill",
            viewportWidth = 30.4102f,
            viewportHeight = 21.8384f
        ) {
            addSfPath("M7.85156 19.1956L21.748 21.6174C24.0332 22.0081 25.5371 20.7776 25.5371 18.5217L25.5371 3.17017C25.5371 0.914307 24.0332-0.325928 21.748 0.0744629L7.85156 2.49634C5.69336 2.86743 4.51172 4.11743 4.51172 6.0022L4.51172 15.6897C4.51172 17.5647 5.69336 18.8245 7.85156 19.1956ZM0.859375 11.6663C0.322266 11.6663 0 11.344 0 10.8557C0 10.3381 0.322266 10.0256 0.859375 10.0256L29.1895 10.0256C29.7266 10.0256 30.0488 10.3381 30.0488 10.8557C30.0488 11.344 29.7266 11.6663 29.1895 11.6663Z", fillAlpha = 0.85f)
        }
        return _sFTrapezoidAndLineHorizontalFill!!
    }

private var _sFTrapezoidAndLineHorizontalFill: ImageVector? = null
