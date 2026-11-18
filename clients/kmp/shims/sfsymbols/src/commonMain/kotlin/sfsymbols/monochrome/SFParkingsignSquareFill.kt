package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParkingsignSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFParkingsignSquareFill: ImageVector
    get() {
        if (_sFParkingsignSquareFill != null) {
            return _sFParkingsignSquareFill!!
        }
        _sFParkingsignSquareFill = sfIcon(
            name = "Monochrome.SFParkingsignSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.70117 5.53711C8.13477 5.53711 7.83203 5.94727 7.83203 6.54297L7.83203 16.3672C7.83203 16.9531 8.14453 17.3633 8.71094 17.3633C9.29688 17.3633 9.61914 16.9727 9.61914 16.3672L9.61914 13.252L12.2949 13.252C14.7168 13.252 16.3477 11.6992 16.3477 9.39453C16.3477 7.10938 14.6973 5.53711 12.3047 5.53711ZM14.5801 9.39453C14.5801 10.8984 13.6035 11.8652 12.0801 11.8652L9.61914 11.8652L9.61914 6.92383L12.0801 6.92383C13.5938 6.92383 14.5801 7.90039 14.5801 9.39453Z", fillAlpha = 0.85f)
        }
        return _sFParkingsignSquareFill!!
    }

private var _sFParkingsignSquareFill: ImageVector? = null
