package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeftToprightRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownLeftToprightRectangle: ImageVector
    get() {
        if (_sFArrowDownLeftToprightRectangle != null) {
            return _sFArrowDownLeftToprightRectangle!!
        }
        _sFArrowDownLeftToprightRectangle = sfIcon(
            name = "Monochrome.SFArrowDownLeftToprightRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M25.6641 5.04883C25.8203 4.90234 25.9277 4.6875 25.9277 4.47266C25.9277 3.98438 25.6055 3.66211 25.127 3.66211C24.8633 3.66211 24.668 3.75977 24.5117 3.91602L20.2246 8.19336L18.2031 10.332C17.4902 11.0938 18.4277 12.1191 19.2188 11.377L21.377 9.3457ZM17.998 12.4512L23.877 12.4512C24.3848 12.4414 24.707 12.1387 24.707 11.6699C24.707 11.1914 24.375 10.8691 23.8477 10.8594L21.4062 10.8594L18.4082 11.1719L18.7109 7.93945L18.7109 5.72266C18.7012 5.19531 18.3887 4.86328 17.9102 4.86328C17.4316 4.86328 17.1289 5.18555 17.1191 5.69336L17.1191 11.5625C17.1191 12.1582 17.4121 12.4512 17.998 12.4512Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeftToprightRectangle!!
    }

private var _sFArrowDownLeftToprightRectangle: ImageVector? = null
