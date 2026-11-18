package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFHeartSquare: ImageVector
    get() {
        if (_sFHeartSquare != null) {
            return _sFHeartSquare!!
        }
        _sFHeartSquare = sfIcon(
            name = "Monochrome.SFHeartSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.4668 5.88867C6.34766 5.88867 4.80469 7.5 4.80469 9.69727C4.80469 13.0957 8.4375 16.1719 10.918 17.7344C11.123 17.8613 11.3672 18.0078 11.5137 18.0078C11.6602 18.0078 11.8945 17.8613 12.0703 17.7344C14.5117 16.1035 18.1836 13.0957 18.1836 9.69727C18.1836 7.5 16.6406 5.88867 14.5215 5.88867C13.1934 5.88867 12.1094 6.67969 11.4941 7.80273C10.8789 6.67969 9.81445 5.88867 8.4668 5.88867Z", fillAlpha = 0.85f)
        }
        return _sFHeartSquare!!
    }

private var _sFHeartSquare: ImageVector? = null
