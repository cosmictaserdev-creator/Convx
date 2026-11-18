package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMicrophoneSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFMicrophoneSquareFill: ImageVector
    get() {
        if (_sFMicrophoneSquareFill != null) {
            return _sFMicrophoneSquareFill!!
        }
        _sFMicrophoneSquareFill = sfIcon(
            name = "Monochrome.SFMicrophoneSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM15.1953 10.1367L15.1953 11.7188C15.1953 13.877 13.7012 15.3711 11.4844 15.3711C9.26758 15.3711 7.7832 13.877 7.7832 11.7188L7.7832 10.1367C7.7832 9.81445 7.50977 9.53125 7.17773 9.53125C6.85547 9.53125 6.57227 9.81445 6.57227 10.1367L6.57227 11.7188C6.57227 14.3066 8.30078 16.2402 10.8691 16.5039L10.8691 18.0273L8.26172 18.0273C7.91992 18.0273 7.62695 18.3203 7.62695 18.6621C7.62695 18.9941 7.91992 19.2676 8.26172 19.2676L14.7168 19.2676C15.0488 19.2676 15.3418 18.9844 15.3418 18.6621C15.3418 18.3203 15.0586 18.0273 14.7168 18.0273L12.1094 18.0273L12.1094 16.5039C14.6777 16.2305 16.4062 14.3066 16.4062 11.7188L16.4062 10.1367C16.4062 9.81445 16.123 9.53125 15.8008 9.53125C15.4688 9.53125 15.1953 9.81445 15.1953 10.1367ZM9.33594 6.07422L9.33594 11.6602C9.33594 12.9688 10.2051 13.9648 11.4844 13.9648C12.7637 13.9648 13.6328 12.9688 13.6328 11.6602L13.6328 6.07422C13.6328 4.76562 12.7637 3.78906 11.4844 3.78906C10.2051 3.78906 9.33594 4.76562 9.33594 6.07422Z", fillAlpha = 0.85f)
        }
        return _sFMicrophoneSquareFill!!
    }

private var _sFMicrophoneSquareFill: ImageVector? = null
