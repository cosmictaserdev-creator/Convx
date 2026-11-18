package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownAppFill (monochrome)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Monochrome.SFArrowDownAppFill: ImageVector
    get() {
        if (_sFArrowDownAppFill != null) {
            return _sFArrowDownAppFill!!
        }
        _sFArrowDownAppFill = sfIcon(
            name = "Monochrome.SFArrowDownAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M21.1523 1.74805C22.3145 2.90039 22.9102 4.60938 22.9102 6.81641L22.9102 16.084C22.9102 18.291 22.3047 20.0098 21.1523 21.1523C20.0293 22.2754 18.3105 22.9004 16.0938 22.9004L6.81641 22.9004C4.59961 22.9004 2.89062 22.2852 1.75781 21.1523C0.595703 20 0 18.291 0 16.084L0 6.81641C0 4.60938 0.605469 2.89062 1.75781 1.74805C2.88086 0.625 4.59961 0 6.81641 0L16.0938 0C18.3105 0 20.0195 0.605469 21.1523 1.74805ZM10.5664 5.83984L10.5664 13.5449L10.6247 15.4391L9.79492 14.5703L7.67578 12.3535C7.51953 12.168 7.27539 12.0801 7.06055 12.0801C6.60156 12.0801 6.25977 12.4121 6.25977 12.8613C6.25977 13.0957 6.35742 13.2812 6.52344 13.4473L10.8105 17.6074C11.0352 17.832 11.2207 17.9102 11.4551 17.9102C11.6797 17.9102 11.875 17.832 12.0898 17.6074L16.3672 13.4473C16.543 13.2812 16.6309 13.0957 16.6309 12.8613C16.6309 12.4121 16.2793 12.0801 15.8301 12.0801C15.6152 12.0801 15.3809 12.168 15.2246 12.3535L13.1055 14.5703L12.2831 15.4365L12.334 13.5449L12.334 5.83984C12.334 5.37109 11.9336 4.98047 11.4551 4.98047C10.9668 4.98047 10.5664 5.37109 10.5664 5.83984Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownAppFill!!
    }

private var _sFArrowDownAppFill: ImageVector? = null
