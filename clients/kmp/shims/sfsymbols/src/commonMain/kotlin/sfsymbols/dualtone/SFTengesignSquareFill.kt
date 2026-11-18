package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTengesignSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFTengesignSquareFill: ImageVector
    get() {
        if (_sFTengesignSquareFill != null) {
            return _sFTengesignSquareFill!!
        }
        _sFTengesignSquareFill = sfIcon(
            name = "Dualtone.SFTengesignSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.5039 17.4316C11.123 17.4316 10.8301 17.1387 10.8301 16.7383L10.8301 9.11133L7.65625 9.11133C7.34375 9.11133 7.11914 8.87695 7.11914 8.58398C7.11914 8.29102 7.34375 8.05664 7.65625 8.05664L15.3223 8.05664C15.625 8.05664 15.8594 8.29102 15.8594 8.58398C15.8594 8.87695 15.625 9.11133 15.3223 9.11133L12.168 9.11133L12.168 16.7383C12.168 17.1387 11.8848 17.4316 11.5039 17.4316ZM7.08008 6.16211C7.08008 5.94727 7.26562 5.76172 7.48047 5.76172L15.459 5.76172C15.6738 5.76172 15.8594 5.94727 15.8594 6.16211C15.8594 6.36719 15.6738 6.55273 15.459 6.55273L7.48047 6.55273C7.26562 6.55273 7.08008 6.38672 7.08008 6.16211Z", fillAlpha = 0.85f)
        }
        return _sFTengesignSquareFill!!
    }

private var _sFTengesignSquareFill: ImageVector? = null
