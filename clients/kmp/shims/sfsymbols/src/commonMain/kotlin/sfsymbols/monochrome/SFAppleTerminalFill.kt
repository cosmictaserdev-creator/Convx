package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppleTerminalFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFAppleTerminalFill: ImageVector
    get() {
        if (_sFAppleTerminalFill != null) {
            return _sFAppleTerminalFill!!
        }
        _sFAppleTerminalFill = sfIcon(
            name = "Monochrome.SFAppleTerminalFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM11.2305 10.2441C10.8203 10.2441 10.5078 10.5664 10.5078 10.9766C10.5078 11.3867 10.8203 11.709 11.2305 11.709L16.1426 11.709C16.5625 11.709 16.8848 11.3867 16.8848 10.9766C16.8848 10.5664 16.5625 10.2441 16.1426 10.2441ZM5.16602 5.71289L8.39844 7.66602L5.16602 9.62891C4.26758 10.1562 5.01953 11.5137 5.99609 10.8984L9.74609 8.54492C10.4004 8.14453 10.3613 7.17773 9.74609 6.77734L5.99609 4.43359C5.01953 3.81836 4.24805 5.15625 5.16602 5.71289Z", fillAlpha = 0.85f)
        }
        return _sFAppleTerminalFill!!
    }

private var _sFAppleTerminalFill: ImageVector? = null
