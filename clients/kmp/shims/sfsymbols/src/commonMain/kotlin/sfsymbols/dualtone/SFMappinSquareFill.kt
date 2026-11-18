package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappinSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFMappinSquareFill: ImageVector
    get() {
        if (_sFMappinSquareFill != null) {
            return _sFMappinSquareFill!!
        }
        _sFMappinSquareFill = sfIcon(
            name = "Dualtone.SFMappinSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.66211 6.51367C8.66211 4.96094 9.93164 3.70117 11.4844 3.70117C13.0371 3.70117 14.3066 4.96094 14.3066 6.51367C14.3066 7.8125 13.4375 8.87695 12.2559 9.19922L12.2559 16.2695C12.2559 18.0469 11.9824 19.4727 11.4844 19.4727C10.9863 19.4727 10.7031 18.0176 10.7031 16.2695L10.7031 9.19922C9.53125 8.86719 8.66211 7.80273 8.66211 6.51367ZM9.85352 5.78125C9.85352 6.26953 10.2832 6.69922 10.7617 6.69922C11.2598 6.69922 11.6602 6.26953 11.6602 5.78125C11.6699 5.29297 11.2598 4.87305 10.7617 4.87305C10.2832 4.87305 9.84375 5.29297 9.85352 5.78125Z", fillAlpha = 0.85f)
        }
        return _sFMappinSquareFill!!
    }

private var _sFMappinSquareFill: ImageVector? = null
