package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisMessageFill (monochrome)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Monochrome.SFEllipsisMessageFill: ImageVector
    get() {
        if (_sFEllipsisMessageFill != null) {
            return _sFEllipsisMessageFill!!
        }
        _sFEllipsisMessageFill = sfIcon(
            name = "Monochrome.SFEllipsisMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M28.6621 11.9043C28.6621 18.7891 22.6465 23.8086 14.3262 23.8086C11.5625 23.8086 9.02344 23.2715 6.875 22.2559C5.60547 23.2031 3.82812 23.8086 2.13867 23.8086C1.41602 23.8086 1.17188 23.2324 1.66016 22.793C2.41211 22.1191 2.75391 21.4746 2.75391 20.4785C2.75391 18.2129 0 16.8848 0 11.9043C0 4.99023 6.01562 0 14.3262 0C22.6367 0 28.6621 4.99023 28.6621 11.9043ZM18.6426 11.9922C18.6426 12.9883 19.4434 13.7891 20.4297 13.7891C21.416 13.7891 22.2266 12.9883 22.2266 11.9922C22.2266 11.0059 21.416 10.2051 20.4297 10.2051C19.4434 10.2051 18.6426 11.0059 18.6426 11.9922ZM12.6562 11.9922C12.6562 12.9883 13.457 13.7891 14.4531 13.7891C15.4395 13.7891 16.2402 12.9883 16.2402 11.9922C16.2402 11.0059 15.4395 10.2051 14.4531 10.2051C13.457 10.2051 12.6562 11.0059 12.6562 11.9922ZM6.66992 11.9922C6.66992 12.9883 7.48047 13.7891 8.4668 13.7891C9.45312 13.7891 10.2539 12.9883 10.2539 11.9922C10.2539 11.0059 9.44336 10.2051 8.4668 10.2051C7.48047 10.2051 6.66992 11.0059 6.66992 11.9922Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisMessageFill!!
    }

private var _sFEllipsisMessageFill: ImageVector? = null
