package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareAndArrowDownFill (monochrome)
 * Viewport: 22.4512 x 27.2949
 */
public val SfSymbols.Monochrome.SFSquareAndArrowDownFill: ImageVector
    get() {
        if (_sFSquareAndArrowDownFill != null) {
            return _sFSquareAndArrowDownFill!!
        }
        _sFSquareAndArrowDownFill = sfIcon(
            name = "Monochrome.SFSquareAndArrowDownFill",
            viewportWidth = 22.4512f,
            viewportHeight = 27.2949f
        ) {
            addSfPath("M5.20508 26.5039L16.8848 26.5039C20.2344 26.5039 22.0898 24.6387 22.0898 21.2988L22.0898 11.6602C22.0898 8.32031 20.2344 6.46484 16.8848 6.46484L5.20508 6.46484C1.85547 6.46484 0 8.32031 0 11.6602L0 21.2988C0 24.6387 1.85547 26.5039 5.20508 26.5039ZM11.0547 0C11.5137 0 11.9043 0.380859 11.9043 0.839844L11.9043 13.9453L11.8457 15.791L12.6074 15L14.7363 12.7637C14.8926 12.5977 15.1172 12.5098 15.3223 12.5098C15.7617 12.5098 16.1035 12.832 16.1035 13.2617C16.1035 13.4961 16.0156 13.6719 15.8496 13.8281L11.6699 17.9004C11.4551 18.1055 11.2695 18.1836 11.0547 18.1836C10.8301 18.1836 10.6445 18.1055 10.4297 17.9004L6.25 13.8281C6.08398 13.6719 5.98633 13.4961 5.98633 13.2617C5.98633 12.832 6.31836 12.5098 6.76758 12.5098C6.97266 12.5098 7.20703 12.5977 7.36328 12.7637L9.48242 15L10.2637 15.8008L10.1953 13.9453L10.1953 0.839844C10.1953 0.380859 10.5859 0 11.0547 0Z", fillAlpha = 0.85f)
        }
        return _sFSquareAndArrowDownFill!!
    }

private var _sFSquareAndArrowDownFill: ImageVector? = null
