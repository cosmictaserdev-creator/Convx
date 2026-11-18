package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkAppFill (monochrome)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Monochrome.SFCheckmarkAppFill: ImageVector
    get() {
        if (_sFCheckmarkAppFill != null) {
            return _sFCheckmarkAppFill!!
        }
        _sFCheckmarkAppFill = sfIcon(
            name = "Monochrome.SFCheckmarkAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M21.1523 1.74805C22.3145 2.90039 22.9102 4.60938 22.9102 6.81641L22.9102 16.084C22.9102 18.291 22.3047 20.0098 21.1523 21.1523C20.0293 22.2754 18.3105 22.9004 16.0938 22.9004L6.81641 22.9004C4.59961 22.9004 2.89062 22.2852 1.75781 21.1523C0.595703 20 0 18.291 0 16.084L0 6.81641C0 4.60938 0.605469 2.89062 1.75781 1.74805C2.88086 0.625 4.59961 0 6.81641 0L16.0938 0C18.3105 0 20.0195 0.605469 21.1523 1.74805ZM15.6445 6.42578L10 15.4004L7.1875 11.8848C6.93359 11.543 6.68945 11.4355 6.37695 11.4355C5.89844 11.4355 5.51758 11.8359 5.51758 12.3242C5.51758 12.5684 5.61523 12.8125 5.78125 13.0273L9.10156 17.0508C9.38477 17.4121 9.67773 17.5684 10.0488 17.5684C10.4102 17.5684 10.7227 17.3926 10.9473 17.0508L17.0605 7.45117C17.1875 7.24609 17.3242 6.99219 17.3242 6.75781C17.3242 6.25977 16.8848 5.92773 16.416 5.92773C16.1328 5.92773 15.8496 6.10352 15.6445 6.42578Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkAppFill!!
    }

private var _sFCheckmarkAppFill: ImageVector? = null
