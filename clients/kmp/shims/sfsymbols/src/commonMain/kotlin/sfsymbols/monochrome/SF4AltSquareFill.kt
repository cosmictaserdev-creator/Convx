package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4AltSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF4AltSquareFill: ImageVector
    get() {
        if (_sF4AltSquareFill != null) {
            return _sF4AltSquareFill!!
        }
        _sF4AltSquareFill = sfIcon(
            name = "Monochrome.SF4AltSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM11.0254 5.99609C9.6875 8.32031 8.30078 10.752 6.94336 13.1543C6.81641 13.3984 6.75781 13.6328 6.75781 13.877C6.75781 14.4336 7.13867 14.7949 7.75391 14.7949L12.666 14.7949L12.666 16.3867C12.666 16.9922 12.9785 17.334 13.4863 17.334C14.0137 17.334 14.3262 17.002 14.3262 16.3867L14.3262 14.7949L15.3223 14.7949C15.8203 14.7949 16.1328 14.4922 16.1328 14.0234C16.1328 13.5938 15.8203 13.3008 15.3223 13.3008L14.3262 13.3008L14.3262 10.3711C14.3262 9.76562 14.0137 9.43359 13.4863 9.43359C12.9785 9.43359 12.666 9.77539 12.666 10.3711L12.666 13.3008L8.85742 13.3008C10 11.1621 11.2793 9.00391 12.4707 6.79688C12.5586 6.61133 12.6172 6.45508 12.6172 6.26953C12.6172 5.83984 12.3438 5.44922 11.8164 5.44922C11.4648 5.44922 11.2305 5.625 11.0254 5.99609Z", fillAlpha = 0.85f)
        }
        return _sF4AltSquareFill!!
    }

private var _sF4AltSquareFill: ImageVector? = null
