package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronUpSquareFill: ImageVector
    get() {
        if (_sFChevronUpSquareFill != null) {
            return _sFChevronUpSquareFill!!
        }
        _sFChevronUpSquareFill = sfIcon(
            name = "Dualtone.SFChevronUpSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M5.12695 14.4434C4.81445 14.1406 4.81445 13.6133 5.16602 13.2617L10.3906 7.75391C11.0938 7.02148 11.8848 7.02148 12.5879 7.75391L17.8223 13.2617C18.1641 13.6133 18.1738 14.1406 17.8613 14.4434C17.5195 14.7949 16.9727 14.7949 16.6602 14.4531L11.4941 9.0332L6.32812 14.4531C6.00586 14.7949 5.46875 14.7949 5.12695 14.4434Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpSquareFill!!
    }

private var _sFChevronUpSquareFill: ImageVector? = null
