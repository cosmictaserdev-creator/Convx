package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFChevronDownSquareFill: ImageVector
    get() {
        if (_sFChevronDownSquareFill != null) {
            return _sFChevronDownSquareFill!!
        }
        _sFChevronDownSquareFill = sfIcon(
            name = "Monochrome.SFChevronDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.12695 8.94531C4.81445 9.27734 4.82422 9.78516 5.16602 10.1465L10.3906 15.6445C11.0938 16.3867 11.9043 16.377 12.5977 15.6445L17.832 10.1465C18.1738 9.78516 18.1934 9.26758 17.8711 8.94531C17.5195 8.60352 16.9824 8.58398 16.6602 8.93555L11.4941 14.3555L6.33789 8.93555C6.01562 8.58398 5.46875 8.59375 5.12695 8.94531Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownSquareFill!!
    }

private var _sFChevronDownSquareFill: ImageVector? = null
