package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFStarSquareFill: ImageVector
    get() {
        if (_sFStarSquareFill != null) {
            return _sFStarSquareFill!!
        }
        _sFStarSquareFill = sfIcon(
            name = "Dualtone.SFStarSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.70508 18.1348C7.04102 18.6328 6.32812 18.1152 6.5918 17.3242L8.07617 12.8809L4.26758 10.1562C3.64258 9.70703 3.85742 8.80859 4.69727 8.81836L9.375 8.84766L10.8008 4.375C11.0449 3.61328 11.9238 3.61328 12.168 4.375L13.5938 8.84766L18.2715 8.81836C19.1309 8.80859 19.3066 9.72656 18.7109 10.1465L14.9023 12.8809L16.377 17.3242C16.6406 18.1152 15.9277 18.6328 15.2637 18.1348L11.4844 15.3711Z", fillAlpha = 0.85f)
        }
        return _sFStarSquareFill!!
    }

private var _sFStarSquareFill: ImageVector? = null
