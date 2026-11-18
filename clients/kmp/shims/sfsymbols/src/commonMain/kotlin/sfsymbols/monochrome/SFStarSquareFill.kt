package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFStarSquareFill: ImageVector
    get() {
        if (_sFStarSquareFill != null) {
            return _sFStarSquareFill!!
        }
        _sFStarSquareFill = sfIcon(
            name = "Monochrome.SFStarSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.8008 4.375L9.375 8.84766L4.69727 8.81836C3.85742 8.80859 3.64258 9.70703 4.26758 10.1562L8.07617 12.8809L6.5918 17.3242C6.32812 18.1152 7.04102 18.6328 7.70508 18.1348L11.4844 15.3711L15.2637 18.1348C15.9277 18.6328 16.6406 18.1152 16.377 17.3242L14.9023 12.8809L18.7109 10.1465C19.3066 9.72656 19.1309 8.80859 18.2715 8.81836L13.5938 8.84766L12.168 4.375C11.9238 3.61328 11.0449 3.61328 10.8008 4.375Z", fillAlpha = 0.85f)
        }
        return _sFStarSquareFill!!
    }

private var _sFStarSquareFill: ImageVector? = null
