package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpadGen1Landscape (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFIpadGen1Landscape: ImageVector
    get() {
        if (_sFIpadGen1Landscape != null) {
            return _sFIpadGen1Landscape!!
        }
        _sFIpadGen1Landscape = sfIcon(
            name = "Monochrome.SFIpadGen1Landscape",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.709 29.5898 19.2188L29.5898 3.78906C29.5898 1.29883 28.3105 0.0292969 25.7812 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM2.17773 12.168C1.81641 12.1777 1.51367 11.8555 1.50391 11.4941C1.49414 11.1426 1.80664 10.8301 2.16797 10.8301C2.53906 10.8301 2.8418 11.1426 2.8418 11.4941C2.8418 11.8555 2.5293 12.1582 2.17773 12.168ZM4.10156 21.25L4.10156 1.75781L25.4883 1.75781L25.4883 21.25ZM27.3242 12.5391C26.748 12.5391 26.2988 12.0703 26.2988 11.4844C26.2988 10.918 26.7578 10.4492 27.334 10.4492C27.9102 10.4492 28.3789 10.918 28.3789 11.4844C28.3789 12.0703 27.9102 12.5391 27.3242 12.5391Z", fillAlpha = 0.85f)
        }
        return _sFIpadGen1Landscape!!
    }

private var _sFIpadGen1Landscape: ImageVector? = null
