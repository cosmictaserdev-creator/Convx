package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFDivideSquare: ImageVector
    get() {
        if (_sFDivideSquare != null) {
            return _sFDivideSquare!!
        }
        _sFDivideSquare = sfIcon(
            name = "Monochrome.SFDivideSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4648 8.17383C12.2559 8.17383 12.8418 7.63672 12.8418 6.93359C12.8418 6.15234 12.2656 5.5957 11.4648 5.5957C10.7227 5.5957 10.1367 6.16211 10.1367 6.94336C10.1367 7.64648 10.7227 8.17383 11.4648 8.17383ZM6.39648 12.3828L16.582 12.3828C17.1777 12.3828 17.5781 12.0703 17.5781 11.4941C17.5781 10.918 17.1973 10.5957 16.582 10.5957L6.39648 10.5957C5.78125 10.5957 5.39062 10.918 5.39062 11.4941C5.39062 12.0703 5.80078 12.3828 6.39648 12.3828ZM11.4648 17.3828C12.2559 17.3828 12.8418 16.8262 12.8418 16.123C12.8418 15.3418 12.2656 14.7949 11.4648 14.7949C10.7227 14.7949 10.1367 15.3516 10.1367 16.1328C10.1367 16.8359 10.7227 17.3828 11.4648 17.3828Z", fillAlpha = 0.85f)
        }
        return _sFDivideSquare!!
    }

private var _sFDivideSquare: ImageVector? = null
