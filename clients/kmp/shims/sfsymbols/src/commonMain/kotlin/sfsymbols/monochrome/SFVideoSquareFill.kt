package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFVideoSquareFill: ImageVector
    get() {
        if (_sFVideoSquareFill != null) {
            return _sFVideoSquareFill!!
        }
        _sFVideoSquareFill = sfIcon(
            name = "Monochrome.SFVideoSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.18164 6.31836C4.94141 6.31836 4.14062 7.05078 4.14062 8.33984L4.14062 14.6094C4.14062 15.8887 4.90234 16.6309 6.18164 16.6309L13.0078 16.6309C14.2969 16.6309 15.0488 15.8887 15.0488 14.6094L15.0488 8.33984C15.0488 7.05078 14.3555 6.31836 13.0566 6.31836ZM18.2227 7.62695L15.7617 9.69727L15.7617 13.2422L18.2227 15.3125C18.4473 15.5078 18.6914 15.625 18.916 15.625C19.3848 15.625 19.6875 15.293 19.6875 14.7656L19.6875 8.17383C19.6875 7.65625 19.3848 7.33398 18.916 7.33398C18.6914 7.33398 18.4277 7.44141 18.2227 7.62695Z", fillAlpha = 0.85f)
        }
        return _sFVideoSquareFill!!
    }

private var _sFVideoSquareFill: ImageVector? = null
