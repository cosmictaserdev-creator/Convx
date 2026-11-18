package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFManatsignSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFManatsignSquareFill: ImageVector
    get() {
        if (_sFManatsignSquareFill != null) {
            return _sFManatsignSquareFill!!
        }
        _sFManatsignSquareFill = sfIcon(
            name = "Monochrome.SFManatsignSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.9863 5.49805L10.9863 6.99219C8.35938 7.22656 7.11914 9.43359 7.11914 12.2949L7.11914 16.3379C7.11914 16.7578 7.37305 17.0312 7.7832 17.0312C8.21289 17.0312 8.4668 16.7578 8.4668 16.3379L8.4668 12.3828C8.4668 10.2344 9.17969 8.48633 10.9863 8.21289L10.9863 16.543C10.9863 16.8457 11.1816 17.0312 11.4844 17.0312C11.7773 17.0312 11.9531 16.8457 11.9531 16.543L11.9531 8.20312C13.8477 8.44727 14.5508 10.2344 14.5508 12.3828L14.5508 16.3379C14.5508 16.7578 14.8047 17.0312 15.2246 17.0312C15.6445 17.0312 15.8887 16.7578 15.8887 16.3379L15.8887 12.2949C15.8887 9.38477 14.6191 7.20703 11.9531 6.98242L11.9531 5.49805C11.9531 5.19531 11.7773 5.01953 11.4844 5.01953C11.1816 5.01953 10.9863 5.19531 10.9863 5.49805Z", fillAlpha = 0.85f)
        }
        return _sFManatsignSquareFill!!
    }

private var _sFManatsignSquareFill: ImageVector? = null
