package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledPatternDiagonallineRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFCircleFilledPatternDiagonallineRectangle: ImageVector
    get() {
        if (_sFCircleFilledPatternDiagonallineRectangle != null) {
            return _sFCircleFilledPatternDiagonallineRectangle!!
        }
        _sFCircleFilledPatternDiagonallineRectangle = sfIcon(
            name = "Monochrome.SFCircleFilledPatternDiagonallineRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M14.668 18.3887C18.4863 18.3887 21.5625 15.3125 21.5625 11.4941C21.5625 7.67578 18.4863 4.59961 14.668 4.59961C10.8594 4.59961 7.77344 7.67578 7.77344 11.4941C7.77344 15.3125 10.8594 18.3887 14.668 18.3887ZM14.668 16.9727C11.6016 16.9727 9.18945 14.5605 9.18945 11.4941C9.18945 8.42773 11.6016 6.01562 14.668 6.01562C17.7344 6.01562 20.1562 8.42773 20.1562 11.4941C20.1562 14.5605 17.7344 16.9727 14.668 16.9727ZM9.59961 14.5215L17.5293 6.5918L16.2793 5.56641L9.14062 12.7051ZM13.2031 17.5391L20.3027 10.4492L19.8047 8.67188L11.9824 16.4844Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledPatternDiagonallineRectangle!!
    }

private var _sFCircleFilledPatternDiagonallineRectangle: ImageVector? = null
