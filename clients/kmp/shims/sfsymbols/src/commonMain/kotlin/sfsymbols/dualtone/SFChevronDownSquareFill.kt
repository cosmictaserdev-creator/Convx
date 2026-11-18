package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronDownSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronDownSquareFill: ImageVector
    get() {
        if (_sFChevronDownSquareFill != null) {
            return _sFChevronDownSquareFill!!
        }
        _sFChevronDownSquareFill = sfIcon(
            name = "Dualtone.SFChevronDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M12.5977 15.6445C11.9043 16.377 11.0938 16.3867 10.3906 15.6445L5.16602 10.1465C4.82422 9.78516 4.81445 9.27734 5.12695 8.94531C5.46875 8.59375 6.01562 8.58398 6.33789 8.93555L11.4941 14.3555L16.6602 8.93555C16.9824 8.58398 17.5195 8.60352 17.8711 8.94531C18.1934 9.26758 18.1738 9.78516 17.832 10.1465Z", fillAlpha = 0.85f)
        }
        return _sFChevronDownSquareFill!!
    }

private var _sFChevronDownSquareFill: ImageVector? = null
