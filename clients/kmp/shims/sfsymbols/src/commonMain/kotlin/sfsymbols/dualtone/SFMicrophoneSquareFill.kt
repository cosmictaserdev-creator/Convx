package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMicrophoneSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFMicrophoneSquareFill: ImageVector
    get() {
        if (_sFMicrophoneSquareFill != null) {
            return _sFMicrophoneSquareFill!!
        }
        _sFMicrophoneSquareFill = sfIcon(
            name = "Dualtone.SFMicrophoneSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.26172 19.2676C7.91992 19.2676 7.62695 18.9941 7.62695 18.6621C7.62695 18.3203 7.91992 18.0273 8.26172 18.0273L10.8691 18.0273L10.8691 16.5039C8.30078 16.2402 6.57227 14.3066 6.57227 11.7188L6.57227 10.1367C6.57227 9.81445 6.85547 9.53125 7.17773 9.53125C7.50977 9.53125 7.7832 9.81445 7.7832 10.1367L7.7832 11.7188C7.7832 13.877 9.26758 15.3711 11.4844 15.3711C13.7012 15.3711 15.1953 13.877 15.1953 11.7188L15.1953 10.1367C15.1953 9.81445 15.4688 9.53125 15.8008 9.53125C16.123 9.53125 16.4062 9.81445 16.4062 10.1367L16.4062 11.7188C16.4062 14.3066 14.6777 16.2305 12.1094 16.5039L12.1094 18.0273L14.7168 18.0273C15.0586 18.0273 15.3418 18.3203 15.3418 18.6621C15.3418 18.9844 15.0488 19.2676 14.7168 19.2676ZM11.4844 13.9648C10.2051 13.9648 9.33594 12.9688 9.33594 11.6602L9.33594 6.07422C9.33594 4.76562 10.2051 3.78906 11.4844 3.78906C12.7637 3.78906 13.6328 4.76562 13.6328 6.07422L13.6328 11.6602C13.6328 12.9688 12.7637 13.9648 11.4844 13.9648Z", fillAlpha = 0.85f)
        }
        return _sFMicrophoneSquareFill!!
    }

private var _sFMicrophoneSquareFill: ImageVector? = null
