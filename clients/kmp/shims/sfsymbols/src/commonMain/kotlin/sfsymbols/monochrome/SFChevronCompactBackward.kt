package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronCompactBackward (monochrome)
 * Viewport: 6.26953 x 22.1191
 */
public val SfSymbols.Monochrome.SFChevronCompactBackward: ImageVector
    get() {
        if (_sFChevronCompactBackward != null) {
            return _sFChevronCompactBackward!!
        }
        _sFChevronCompactBackward = sfIcon(
            name = "Monochrome.SFChevronCompactBackward",
            viewportWidth = 6.26953f,
            viewportHeight = 22.1191f
        ) {
            addSfPath("M3.91602 21.3867C4.08203 21.8066 4.44336 22.0605 4.82422 22.0605C5.43945 22.0605 5.9082 21.6016 5.9082 20.9863C5.9082 20.6641 5.73242 20.2539 5.63477 19.9805L1.78711 10.293L1.78711 11.7578L5.63477 2.07031C5.73242 1.79688 5.9082 1.37695 5.9082 1.07422C5.9082 0.458984 5.43945 0 4.82422 0C4.44336 0 4.08203 0.253906 3.91602 0.664062L0.419922 9.48242C0.224609 9.96094 0 10.5664 0 11.0254C0 11.4941 0.224609 12.0801 0.419922 12.5684Z", fillAlpha = 0.85f)
        }
        return _sFChevronCompactBackward!!
    }

private var _sFChevronCompactBackward: ImageVector? = null
