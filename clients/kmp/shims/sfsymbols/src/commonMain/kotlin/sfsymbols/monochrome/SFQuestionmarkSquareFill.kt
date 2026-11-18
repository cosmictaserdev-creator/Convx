package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQuestionmarkSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFQuestionmarkSquareFill: ImageVector
    get() {
        if (_sFQuestionmarkSquareFill != null) {
            return _sFQuestionmarkSquareFill!!
        }
        _sFQuestionmarkSquareFill = sfIcon(
            name = "Monochrome.SFQuestionmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.93164 16.6406C9.93164 17.3145 10.5176 17.8711 11.2109 17.8711C11.8945 17.8711 12.4902 17.3242 12.4902 16.6406C12.4902 15.9473 11.9043 15.4004 11.2109 15.4004C10.5078 15.4004 9.93164 15.957 9.93164 16.6406ZM7.70508 7.32422C7.65625 7.51953 7.61719 7.71484 7.61719 7.92969C7.61719 8.42773 8.00781 8.71094 8.38867 8.71094C8.7793 8.71094 9.0332 8.51562 9.21875 8.23242L9.4043 7.95898C9.76562 7.11914 10.5273 6.55273 11.5332 6.55273C12.793 6.55273 13.6133 7.25586 13.6133 8.28125C13.6133 9.16016 13.0957 9.61914 11.9336 10.4297C10.9766 11.1035 10.3418 11.7773 10.3418 12.998C10.3418 13.0762 10.3418 13.1543 10.3418 13.2324C10.3418 13.8184 10.6641 14.1211 11.2305 14.1211C11.7969 14.1211 12.1289 13.7695 12.1289 13.3105C12.1289 13.2422 12.1289 13.1641 12.1289 13.0957C12.1289 12.3438 12.5391 11.8848 13.4668 11.2598C14.7461 10.4102 15.5566 9.6582 15.5566 8.19336C15.5566 6.17188 13.75 5.01953 11.6016 5.01953C9.43359 5.01953 8.01758 6.10352 7.70508 7.32422Z", fillAlpha = 0.85f)
        }
        return _sFQuestionmarkSquareFill!!
    }

private var _sFQuestionmarkSquareFill: ImageVector? = null
