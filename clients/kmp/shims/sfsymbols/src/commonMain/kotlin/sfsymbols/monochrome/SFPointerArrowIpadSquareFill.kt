package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPointerArrowIpadSquareFill: ImageVector
    get() {
        if (_sFPointerArrowIpadSquareFill != null) {
            return _sFPointerArrowIpadSquareFill!!
        }
        _sFPointerArrowIpadSquareFill = sfIcon(
            name = "Monochrome.SFPointerArrowIpadSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.97266 5.39062L6.97266 18.0078C6.97266 18.916 7.94922 19.3066 8.70117 18.5645L12.0703 15.1953L16.8164 15.1953C17.8906 15.1953 18.291 14.2285 17.6465 13.584L8.75977 4.69727C8.05664 3.99414 6.97266 4.36523 6.97266 5.39062Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSquareFill!!
    }

private var _sFPointerArrowIpadSquareFill: ImageVector? = null
