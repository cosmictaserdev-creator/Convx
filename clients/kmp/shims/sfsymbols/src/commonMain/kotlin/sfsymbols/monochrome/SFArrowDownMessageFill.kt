package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownMessageFill (monochrome)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Monochrome.SFArrowDownMessageFill: ImageVector
    get() {
        if (_sFArrowDownMessageFill != null) {
            return _sFArrowDownMessageFill!!
        }
        _sFArrowDownMessageFill = sfIcon(
            name = "Monochrome.SFArrowDownMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M28.6621 11.9043C28.6621 18.7891 22.6465 23.8086 14.3262 23.8086C11.5625 23.8086 9.02344 23.2715 6.875 22.2559C5.60547 23.2031 3.82812 23.8086 2.13867 23.8086C1.41602 23.8086 1.17188 23.2324 1.66016 22.793C2.41211 22.1191 2.75391 21.4746 2.75391 20.4785C2.75391 18.2129 0 16.8848 0 11.9043C0 4.99023 6.01562 0 14.3262 0C22.6367 0 28.6621 4.99023 28.6621 11.9043ZM13.5742 6.40625L13.5742 14.1113L13.6325 16.0055L12.8027 15.1367L10.6836 12.9199C10.5273 12.7344 10.2832 12.6465 10.0586 12.6465C9.60938 12.6465 9.26758 12.9785 9.26758 13.4277C9.26758 13.6621 9.36523 13.8477 9.53125 14.0137L13.8184 18.1738C14.043 18.3984 14.2285 18.4668 14.4629 18.4668C14.6875 18.4668 14.873 18.3984 15.0977 18.1738L19.375 14.0137C19.5508 13.8477 19.6387 13.6621 19.6387 13.4277C19.6387 12.9785 19.2871 12.6465 18.8379 12.6465C18.623 12.6465 18.3887 12.7344 18.2324 12.9199L16.1035 15.1367L15.2835 16.0055L15.3418 14.1113L15.3418 6.40625C15.3418 5.9375 14.9414 5.54688 14.4629 5.54688C13.9746 5.54688 13.5742 5.9375 13.5742 6.40625Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownMessageFill!!
    }

private var _sFArrowDownMessageFill: ImageVector? = null
