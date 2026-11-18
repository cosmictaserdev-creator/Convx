package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkMessageFill (dualtone)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Dualtone.SFCheckmarkMessageFill: ImageVector
    get() {
        if (_sFCheckmarkMessageFill != null) {
            return _sFCheckmarkMessageFill!!
        }
        _sFCheckmarkMessageFill = sfIcon(
            name = "Dualtone.SFCheckmarkMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M14.3262 23.8086C22.6465 23.8086 28.6621 18.7891 28.6621 11.9043C28.6621 4.99023 22.6367 0 14.3262 0C6.01562 0 0 4.99023 0 11.9043C0 16.8848 2.75391 18.2129 2.75391 20.4785C2.75391 21.4746 2.41211 22.1191 1.66016 22.793C1.17188 23.2324 1.41602 23.8086 2.13867 23.8086C3.82812 23.8086 5.60547 23.2031 6.875 22.2559C9.02344 23.2715 11.5625 23.8086 14.3262 23.8086Z", fillAlpha = 0.2125f)
            addSfPath("M13.0566 18.125C12.6855 18.125 12.3926 17.9785 12.1094 17.6172L8.7793 13.5938C8.62305 13.3789 8.52539 13.1348 8.52539 12.8906C8.52539 12.3926 8.89648 12.002 9.38477 12.002C9.69727 12.002 9.94141 12.1094 10.1953 12.4512L13.0078 15.9668L18.6523 6.99219C18.8574 6.66016 19.1309 6.49414 19.4238 6.49414C19.8926 6.49414 20.332 6.82617 20.332 7.32422C20.332 7.54883 20.1953 7.80273 20.0684 8.01758L13.9453 17.6172C13.7207 17.959 13.418 18.125 13.0566 18.125Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkMessageFill!!
    }

private var _sFCheckmarkMessageFill: ImageVector? = null
