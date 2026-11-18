package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappinSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFMappinSquareFill: ImageVector
    get() {
        if (_sFMappinSquareFill != null) {
            return _sFMappinSquareFill!!
        }
        _sFMappinSquareFill = sfIcon(
            name = "Monochrome.SFMappinSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.66211 6.51367C8.66211 7.80273 9.53125 8.86719 10.7031 9.19922L10.7031 16.2695C10.7031 18.0176 10.9863 19.4727 11.4844 19.4727C11.9824 19.4727 12.2559 18.0469 12.2559 16.2695L12.2559 9.19922C13.4375 8.87695 14.3066 7.8125 14.3066 6.51367C14.3066 4.96094 13.0371 3.70117 11.4844 3.70117C9.93164 3.70117 8.66211 4.96094 8.66211 6.51367ZM11.6602 5.78125C11.6602 6.26953 11.2598 6.69922 10.7617 6.69922C10.2832 6.69922 9.85352 6.26953 9.85352 5.78125C9.84375 5.29297 10.2832 4.87305 10.7617 4.87305C11.2598 4.87305 11.6699 5.29297 11.6602 5.78125Z", fillAlpha = 0.85f)
        }
        return _sFMappinSquareFill!!
    }

private var _sFMappinSquareFill: ImageVector? = null
