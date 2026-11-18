package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkMessageFill (monochrome)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Monochrome.SFCheckmarkMessageFill: ImageVector
    get() {
        if (_sFCheckmarkMessageFill != null) {
            return _sFCheckmarkMessageFill!!
        }
        _sFCheckmarkMessageFill = sfIcon(
            name = "Monochrome.SFCheckmarkMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M28.6621 11.9043C28.6621 18.7891 22.6465 23.8086 14.3262 23.8086C11.5625 23.8086 9.02344 23.2715 6.875 22.2559C5.60547 23.2031 3.82812 23.8086 2.13867 23.8086C1.41602 23.8086 1.17188 23.2324 1.66016 22.793C2.41211 22.1191 2.75391 21.4746 2.75391 20.4785C2.75391 18.2129 0 16.8848 0 11.9043C0 4.99023 6.01562 0 14.3262 0C22.6367 0 28.6621 4.99023 28.6621 11.9043ZM18.6523 6.99219L13.0078 15.9668L10.1953 12.4512C9.94141 12.1094 9.69727 12.002 9.38477 12.002C8.89648 12.002 8.52539 12.3926 8.52539 12.8906C8.52539 13.1348 8.62305 13.3789 8.7793 13.5938L12.1094 17.6172C12.3926 17.9785 12.6855 18.125 13.0566 18.125C13.418 18.125 13.7207 17.959 13.9453 17.6172L20.0684 8.01758C20.1953 7.80273 20.332 7.54883 20.332 7.32422C20.332 6.82617 19.8926 6.49414 19.4238 6.49414C19.1309 6.49414 18.8574 6.66016 18.6523 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkMessageFill!!
    }

private var _sFCheckmarkMessageFill: ImageVector? = null
