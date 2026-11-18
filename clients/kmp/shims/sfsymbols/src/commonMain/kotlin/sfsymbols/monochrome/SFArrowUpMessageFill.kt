package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpMessageFill (monochrome)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Monochrome.SFArrowUpMessageFill: ImageVector
    get() {
        if (_sFArrowUpMessageFill != null) {
            return _sFArrowUpMessageFill!!
        }
        _sFArrowUpMessageFill = sfIcon(
            name = "Monochrome.SFArrowUpMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M28.6621 11.9043C28.6621 18.7891 22.6465 23.8086 14.3262 23.8086C11.5625 23.8086 9.02344 23.2715 6.875 22.2559C5.60547 23.2031 3.82812 23.8086 2.13867 23.8086C1.41602 23.8086 1.17188 23.2324 1.66016 22.793C2.41211 22.1191 2.75391 21.4746 2.75391 20.4785C2.75391 18.2129 0 16.8848 0 11.9043C0 4.99023 6.01562 0 14.3262 0C22.6367 0 28.6621 4.99023 28.6621 11.9043ZM13.8184 5.83984L9.53125 10C9.36523 10.166 9.26758 10.3516 9.26758 10.5859C9.26758 11.0352 9.60938 11.3672 10.0586 11.3672C10.2832 11.3672 10.5273 11.2793 10.6836 11.0938L12.8027 8.87695L13.6326 8.00806L13.5742 9.91211L13.5742 17.6074C13.5742 18.0762 13.9746 18.4668 14.4629 18.4668C14.9414 18.4668 15.3418 18.0762 15.3418 17.6074L15.3418 9.91211L15.2834 8.00806L16.1035 8.87695L18.2324 11.0938C18.3887 11.2793 18.623 11.3672 18.8379 11.3672C19.2871 11.3672 19.6387 11.0352 19.6387 10.5859C19.6387 10.3516 19.5508 10.166 19.375 10L15.0977 5.83984C14.873 5.61523 14.6875 5.54688 14.4629 5.54688C14.2285 5.54688 14.043 5.61523 13.8184 5.83984Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpMessageFill!!
    }

private var _sFArrowUpMessageFill: ImageVector? = null
