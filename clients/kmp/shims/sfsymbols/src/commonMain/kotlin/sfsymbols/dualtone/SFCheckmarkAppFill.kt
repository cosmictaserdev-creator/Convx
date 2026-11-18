package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFCheckmarkAppFill: ImageVector
    get() {
        if (_sFCheckmarkAppFill != null) {
            return _sFCheckmarkAppFill!!
        }
        _sFCheckmarkAppFill = sfIcon(
            name = "Dualtone.SFCheckmarkAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M10.0488 17.5684C9.67773 17.5684 9.38477 17.4121 9.10156 17.0508L5.78125 13.0273C5.61523 12.8125 5.51758 12.5684 5.51758 12.3242C5.51758 11.8359 5.89844 11.4355 6.37695 11.4355C6.68945 11.4355 6.93359 11.543 7.1875 11.8848L10 15.4004L15.6445 6.42578C15.8496 6.10352 16.1328 5.92773 16.416 5.92773C16.8848 5.92773 17.3242 6.25977 17.3242 6.75781C17.3242 6.99219 17.1875 7.24609 17.0605 7.45117L10.9473 17.0508C10.7227 17.3926 10.4102 17.5684 10.0488 17.5684Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkAppFill!!
    }

private var _sFCheckmarkAppFill: ImageVector? = null
