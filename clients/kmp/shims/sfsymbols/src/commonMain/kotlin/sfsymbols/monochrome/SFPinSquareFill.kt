package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPinSquareFill: ImageVector
    get() {
        if (_sFPinSquareFill != null) {
            return _sFPinSquareFill!!
        }
        _sFPinSquareFill = sfIcon(
            name = "Monochrome.SFPinSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.05664 4.46289C7.62695 4.46289 7.32422 4.72656 7.32422 5.10742C7.32422 5.3125 7.44141 5.54688 7.61719 5.79102C7.97852 6.25 8.75977 6.83594 9.61914 7.34375L9.4043 10.459C7.54883 11.1035 6.42578 12.5879 6.42578 14.0039C6.42578 14.5312 6.77734 14.8633 7.31445 14.8633L10.7812 14.8633L10.7812 17.373C10.7812 18.6328 11.2695 19.6973 11.4844 19.6973C11.709 19.6973 12.207 18.6328 12.207 17.373L12.207 14.8633L15.6641 14.8633C16.2109 14.8633 16.5625 14.5312 16.5625 14.0039C16.5625 12.5879 15.4297 11.1035 13.584 10.459L13.3691 7.34375C14.2383 6.83594 15.0293 6.23047 15.3906 5.77148C15.5566 5.53711 15.6543 5.3125 15.6543 5.10742C15.6543 4.72656 15.3711 4.46289 14.9219 4.46289Z", fillAlpha = 0.85f)
        }
        return _sFPinSquareFill!!
    }

private var _sFPinSquareFill: ImageVector? = null
