package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFHeartSquareFill: ImageVector
    get() {
        if (_sFHeartSquareFill != null) {
            return _sFHeartSquareFill!!
        }
        _sFHeartSquareFill = sfIcon(
            name = "Monochrome.SFHeartSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM11.4844 7.69531C10.8496 6.55273 9.76562 5.74219 8.37891 5.74219C6.20117 5.74219 4.62891 7.39258 4.62891 9.64844C4.62891 13.1152 8.34961 16.2695 10.8887 17.8711C11.0938 18.0078 11.3574 18.1543 11.5039 18.1543C11.6602 18.1543 11.8848 18.0078 12.0801 17.8711C14.5703 16.2012 18.3398 13.1152 18.3398 9.64844C18.3398 7.39258 16.7578 5.74219 14.5898 5.74219C13.2129 5.74219 12.1191 6.55273 11.4844 7.69531Z", fillAlpha = 0.85f)
        }
        return _sFHeartSquareFill!!
    }

private var _sFHeartSquareFill: ImageVector? = null
