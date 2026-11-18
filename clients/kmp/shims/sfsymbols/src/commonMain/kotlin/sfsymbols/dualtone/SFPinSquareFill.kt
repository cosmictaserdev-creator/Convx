package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPinSquareFill: ImageVector
    get() {
        if (_sFPinSquareFill != null) {
            return _sFPinSquareFill!!
        }
        _sFPinSquareFill = sfIcon(
            name = "Dualtone.SFPinSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.42578 14.0039C6.42578 12.5879 7.54883 11.1035 9.4043 10.459L9.61914 7.34375C8.75977 6.83594 7.97852 6.25 7.61719 5.79102C7.44141 5.54688 7.32422 5.3125 7.32422 5.10742C7.32422 4.72656 7.62695 4.46289 8.05664 4.46289L14.9219 4.46289C15.3711 4.46289 15.6543 4.72656 15.6543 5.10742C15.6543 5.3125 15.5566 5.53711 15.3906 5.77148C15.0293 6.23047 14.2383 6.83594 13.3691 7.34375L13.584 10.459C15.4297 11.1035 16.5625 12.5879 16.5625 14.0039C16.5625 14.5312 16.2109 14.8633 15.6641 14.8633L12.207 14.8633L12.207 17.373C12.207 18.6328 11.709 19.6973 11.4844 19.6973C11.2695 19.6973 10.7812 18.6328 10.7812 17.373L10.7812 14.8633L7.31445 14.8633C6.77734 14.8633 6.42578 14.5312 6.42578 14.0039Z", fillAlpha = 0.85f)
        }
        return _sFPinSquareFill!!
    }

private var _sFPinSquareFill: ImageVector? = null
