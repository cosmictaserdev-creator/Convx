package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForwardToLineSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowForwardToLineSquareFill: ImageVector
    get() {
        if (_sFArrowForwardToLineSquareFill != null) {
            return _sFArrowForwardToLineSquareFill!!
        }
        _sFArrowForwardToLineSquareFill = sfIcon(
            name = "Monochrome.SFArrowForwardToLineSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM16.5039 6.63086L16.5039 16.3086C16.5039 16.7383 16.8262 17.041 17.2461 17.041C17.6953 17.041 18.0176 16.7383 18.0176 16.3086L18.0176 6.63086C18.0176 6.19141 17.6953 5.88867 17.2461 5.88867C16.8262 5.88867 16.5039 6.19141 16.5039 6.63086ZM9.38477 6.94336C9.38477 7.1582 9.45312 7.36328 9.60938 7.50977L10.6152 8.48633L13.2031 10.8398L10.8203 10.7324L4.59961 10.7324C4.16016 10.7324 3.84766 11.0449 3.84766 11.4746C3.84766 11.9336 4.16016 12.2461 4.59961 12.2461L10.8203 12.2461L13.2129 12.1094L10.6348 14.4531L9.60938 15.4199C9.45312 15.5566 9.38477 15.7422 9.38477 15.9766C9.38477 16.416 9.6875 16.709 10.1172 16.709C10.3223 16.709 10.5566 16.6016 10.7129 16.4453L15.1855 12.041C15.3613 11.875 15.4395 11.6797 15.4395 11.4746C15.4395 11.2695 15.3418 11.0742 15.1855 10.9082L10.6836 6.46484C10.5273 6.28906 10.332 6.2207 10.127 6.2207C9.70703 6.2207 9.38477 6.51367 9.38477 6.94336Z", fillAlpha = 0.85f)
        }
        return _sFArrowForwardToLineSquareFill!!
    }

private var _sFArrowForwardToLineSquareFill: ImageVector? = null
