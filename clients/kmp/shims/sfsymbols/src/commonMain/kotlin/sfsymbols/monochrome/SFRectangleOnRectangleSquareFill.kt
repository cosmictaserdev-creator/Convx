package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleOnRectangleSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFRectangleOnRectangleSquareFill: ImageVector
    get() {
        if (_sFRectangleOnRectangleSquareFill != null) {
            return _sFRectangleOnRectangleSquareFill!!
        }
        _sFRectangleOnRectangleSquareFill = sfIcon(
            name = "Monochrome.SFRectangleOnRectangleSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.47656 7.92969C7.33398 7.92969 6.75781 8.51562 6.75781 9.62891L6.75781 15.7324C6.75781 16.875 7.33398 17.4512 8.47656 17.4512L17.2559 17.4512C18.3984 17.4512 18.9746 16.875 18.9746 15.7324L18.9746 9.62891C18.9746 8.51562 18.3984 7.92969 17.2559 7.92969ZM5.67383 5.29297C4.52148 5.29297 3.95508 5.87891 3.95508 6.99219L3.95508 13.1055C3.95508 14.248 4.52148 14.8047 5.67383 14.8047L6.00586 14.8047L6.00586 9.62891C6.00586 8.0957 6.9043 7.17773 8.47656 7.17773L16.1719 7.17773L16.1719 6.99219C16.1719 5.87891 15.5957 5.29297 14.4531 5.29297Z", fillAlpha = 0.85f)
        }
        return _sFRectangleOnRectangleSquareFill!!
    }

private var _sFRectangleOnRectangleSquareFill: ImageVector? = null
