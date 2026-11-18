package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonBustFill (monochrome)
 * Viewport: 22.0996 x 26.6113
 */
public val SfSymbols.Monochrome.SFPersonBustFill: ImageVector
    get() {
        if (_sFPersonBustFill != null) {
            return _sFPersonBustFill!!
        }
        _sFPersonBustFill = sfIcon(
            name = "Monochrome.SFPersonBustFill",
            viewportWidth = 22.0996f,
            viewportHeight = 26.6113f
        ) {
            addSfPath("M10.8691 9.0918C13.1934 9.11133 15.0391 7.10938 15.0391 4.50195C15.0391 2.05078 13.1836 0 10.8691 0C8.54492 0 6.67969 2.05078 6.68945 4.50195C6.68945 7.10938 8.53516 9.08203 10.8691 9.0918ZM3.50586 18.1641L18.2129 18.1641C18.9941 18.1641 19.4434 17.6758 19.4434 16.9824C19.4434 14.873 16.4844 10.6348 10.8496 10.6348C5.22461 10.6348 2.27539 14.873 2.27539 16.9824C2.27539 17.6758 2.72461 18.1641 3.50586 18.1641ZM0.869141 26.5527L20.8691 26.5527C21.3379 26.5527 21.7383 26.1621 21.7383 25.6836C21.7383 25.2148 21.3379 24.8242 20.8691 24.8242L20.0977 24.8242L20.0977 21.5527C20.0977 20.3809 19.5215 19.7266 18.2715 19.7266L3.4668 19.7266C2.2168 19.7266 1.64062 20.3809 1.64062 21.5527L1.64062 24.8242L0.869141 24.8242C0.390625 24.8242 0 25.2148 0 25.6836C0 26.1621 0.390625 26.5527 0.869141 26.5527ZM8.27148 23.8672C7.88086 23.8672 7.58789 23.5645 7.58789 23.1738C7.58789 22.7832 7.88086 22.5 8.27148 22.5L13.4766 22.5C13.877 22.5 14.1602 22.7832 14.1602 23.1738C14.1602 23.5645 13.877 23.8672 13.4766 23.8672Z", fillAlpha = 0.85f)
        }
        return _sFPersonBustFill!!
    }

private var _sFPersonBustFill: ImageVector? = null
