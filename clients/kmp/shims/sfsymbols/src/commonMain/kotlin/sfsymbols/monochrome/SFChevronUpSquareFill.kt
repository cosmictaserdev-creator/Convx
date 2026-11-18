package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFChevronUpSquareFill: ImageVector
    get() {
        if (_sFChevronUpSquareFill != null) {
            return _sFChevronUpSquareFill!!
        }
        _sFChevronUpSquareFill = sfIcon(
            name = "Monochrome.SFChevronUpSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.3906 7.75391L5.16602 13.2617C4.81445 13.6133 4.81445 14.1406 5.12695 14.4434C5.46875 14.7949 6.00586 14.7949 6.32812 14.4531L11.4941 9.0332L16.6602 14.4531C16.9727 14.7949 17.5195 14.7949 17.8613 14.4434C18.1738 14.1406 18.1641 13.6133 17.8223 13.2617L12.5879 7.75391C11.8848 7.02148 11.0938 7.02148 10.3906 7.75391Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpSquareFill!!
    }

private var _sFChevronUpSquareFill: ImageVector? = null
