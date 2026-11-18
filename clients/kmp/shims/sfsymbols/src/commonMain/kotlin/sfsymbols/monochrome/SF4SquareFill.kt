package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF4SquareFill: ImageVector
    get() {
        if (_sF4SquareFill != null) {
            return _sF4SquareFill!!
        }
        _sF4SquareFill = sfIcon(
            name = "Monochrome.SF4SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.8887 6.35742C9.58984 8.30078 7.75391 11.0059 6.69922 12.7344C6.47461 13.1055 6.39648 13.3887 6.39648 13.7207C6.39648 14.3945 6.8457 14.834 7.57812 14.834L12.3047 14.834L12.3047 16.3965C12.3047 16.9727 12.6172 17.3242 13.125 17.3242C13.6621 17.3242 13.9648 16.9824 13.9648 16.3965L13.9648 14.834L15.0781 14.834C15.5176 14.834 15.8203 14.5312 15.8203 14.1016C15.8203 13.6523 15.5273 13.3496 15.0781 13.3496L13.9648 13.3496L13.9648 6.58203C13.9648 5.86914 13.418 5.39062 12.6074 5.39062C11.8359 5.39062 11.3477 5.66406 10.8887 6.35742ZM12.3047 13.3496L8.06641 13.3496L8.06641 13.2812C9.01367 11.6602 10.9668 8.75977 12.2168 6.88477L12.3047 6.88477Z", fillAlpha = 0.85f)
        }
        return _sF4SquareFill!!
    }

private var _sF4SquareFill: ImageVector? = null
