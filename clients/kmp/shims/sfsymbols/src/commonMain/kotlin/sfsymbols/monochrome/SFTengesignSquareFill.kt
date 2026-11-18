package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesignSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFTengesignSquareFill: ImageVector
    get() {
        if (_sFTengesignSquareFill != null) {
            return _sFTengesignSquareFill!!
        }
        _sFTengesignSquareFill = sfIcon(
            name = "Monochrome.SFTengesignSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.65625 8.05664C7.34375 8.05664 7.11914 8.29102 7.11914 8.58398C7.11914 8.87695 7.34375 9.11133 7.65625 9.11133L10.8301 9.11133L10.8301 16.7383C10.8301 17.1387 11.123 17.4316 11.5039 17.4316C11.8848 17.4316 12.168 17.1387 12.168 16.7383L12.168 9.11133L15.3223 9.11133C15.625 9.11133 15.8594 8.87695 15.8594 8.58398C15.8594 8.29102 15.625 8.05664 15.3223 8.05664ZM7.48047 5.76172C7.26562 5.76172 7.08008 5.94727 7.08008 6.16211C7.08008 6.38672 7.26562 6.55273 7.48047 6.55273L15.459 6.55273C15.6738 6.55273 15.8594 6.36719 15.8594 6.16211C15.8594 5.94727 15.6738 5.76172 15.459 5.76172Z", fillAlpha = 0.85f)
        }
        return _sFTengesignSquareFill!!
    }

private var _sFTengesignSquareFill: ImageVector? = null
