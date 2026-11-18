package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFVideoSquareFill: ImageVector
    get() {
        if (_sFVideoSquareFill != null) {
            return _sFVideoSquareFill!!
        }
        _sFVideoSquareFill = sfIcon(
            name = "Dualtone.SFVideoSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.18164 16.6309C4.90234 16.6309 4.14062 15.8887 4.14062 14.6094L4.14062 8.33984C4.14062 7.05078 4.94141 6.31836 6.18164 6.31836L13.0566 6.31836C14.3555 6.31836 15.0488 7.05078 15.0488 8.33984L15.0488 14.6094C15.0488 15.8887 14.2969 16.6309 13.0078 16.6309ZM15.7617 13.2422L15.7617 9.69727L18.2227 7.62695C18.4277 7.44141 18.6914 7.33398 18.916 7.33398C19.3848 7.33398 19.6875 7.65625 19.6875 8.17383L19.6875 14.7656C19.6875 15.293 19.3848 15.625 18.916 15.625C18.6914 15.625 18.4473 15.5078 18.2227 15.3125Z", fillAlpha = 0.85f)
        }
        return _sFVideoSquareFill!!
    }

private var _sFVideoSquareFill: ImageVector? = null
