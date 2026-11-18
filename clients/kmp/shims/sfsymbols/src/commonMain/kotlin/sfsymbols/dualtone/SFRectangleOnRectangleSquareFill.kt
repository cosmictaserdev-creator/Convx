package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleOnRectangleSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFRectangleOnRectangleSquareFill: ImageVector
    get() {
        if (_sFRectangleOnRectangleSquareFill != null) {
            return _sFRectangleOnRectangleSquareFill!!
        }
        _sFRectangleOnRectangleSquareFill = sfIcon(
            name = "Dualtone.SFRectangleOnRectangleSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M18.9746 9.62891L18.9746 15.7324C18.9746 16.875 18.3984 17.4512 17.2559 17.4512L8.47656 17.4512C7.33398 17.4512 6.75781 16.875 6.75781 15.7324L6.75781 9.62891C6.75781 8.51562 7.33398 7.92969 8.47656 7.92969L17.2559 7.92969C18.3984 7.92969 18.9746 8.51562 18.9746 9.62891ZM16.1719 6.99219L16.1719 7.17773L8.47656 7.17773C6.9043 7.17773 6.00586 8.0957 6.00586 9.62891L6.00586 14.8047L5.67383 14.8047C4.52148 14.8047 3.95508 14.248 3.95508 13.1055L3.95508 6.99219C3.95508 5.87891 4.52148 5.29297 5.67383 5.29297L14.4531 5.29297C15.5957 5.29297 16.1719 5.87891 16.1719 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFRectangleOnRectangleSquareFill!!
    }

private var _sFRectangleOnRectangleSquareFill: ImageVector? = null
