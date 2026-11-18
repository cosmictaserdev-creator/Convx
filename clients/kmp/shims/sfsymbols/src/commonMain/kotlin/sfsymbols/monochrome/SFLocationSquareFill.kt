package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLocationSquareFill: ImageVector
    get() {
        if (_sFLocationSquareFill != null) {
            return _sFLocationSquareFill!!
        }
        _sFLocationSquareFill = sfIcon(
            name = "Monochrome.SFLocationSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM16.6797 5.11719L4.25781 11.0059C3.50586 11.3574 3.64258 12.4414 4.62891 12.4414L10.293 12.4414C10.498 12.4414 10.6348 12.5879 10.6348 12.793L10.6445 18.3984C10.6445 19.4238 11.7285 19.5117 12.0703 18.7793L17.9199 6.34766C18.3496 5.44922 17.5781 4.69727 16.6797 5.11719Z", fillAlpha = 0.85f)
        }
        return _sFLocationSquareFill!!
    }

private var _sFLocationSquareFill: ImageVector? = null
