package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFHeartSquareFill: ImageVector
    get() {
        if (_sFHeartSquareFill != null) {
            return _sFHeartSquareFill!!
        }
        _sFHeartSquareFill = sfIcon(
            name = "Dualtone.SFHeartSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.37891 5.74219C9.76562 5.74219 10.8496 6.55273 11.4844 7.69531C12.1191 6.55273 13.2129 5.74219 14.5898 5.74219C16.7578 5.74219 18.3398 7.39258 18.3398 9.64844C18.3398 13.1152 14.5703 16.2012 12.0801 17.8711C11.8848 18.0078 11.6602 18.1543 11.5039 18.1543C11.3574 18.1543 11.0938 18.0078 10.8887 17.8711C8.34961 16.2695 4.62891 13.1152 4.62891 9.64844C4.62891 7.39258 6.20117 5.74219 8.37891 5.74219Z", fillAlpha = 0.85f)
        }
        return _sFHeartSquareFill!!
    }

private var _sFHeartSquareFill: ImageVector? = null
