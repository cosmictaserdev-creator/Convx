package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessageFill (dualtone)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Dualtone.SFMessageFill: ImageVector
    get() {
        if (_sFMessageFill != null) {
            return _sFMessageFill!!
        }
        _sFMessageFill = sfIcon(
            name = "Dualtone.SFMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M14.3262 23.8086C22.6465 23.8086 28.6621 18.7891 28.6621 11.9043C28.6621 4.99023 22.6367 0 14.3262 0C6.01562 0 0 4.99023 0 11.9043C0 16.8848 2.75391 18.2129 2.75391 20.4785C2.75391 21.4746 2.41211 22.1191 1.66016 22.793C1.17188 23.2324 1.41602 23.8086 2.13867 23.8086C3.82812 23.8086 5.60547 23.2031 6.875 22.2559C9.02344 23.2715 11.5625 23.8086 14.3262 23.8086Z", fillAlpha = 0.85f)
        }
        return _sFMessageFill!!
    }

private var _sFMessageFill: ImageVector? = null
