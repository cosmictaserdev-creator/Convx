package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltFill (monochrome)
 * Viewport: 17.6953 x 27.9005
 */
public val SfSymbols.Monochrome.SFBoltFill: ImageVector
    get() {
        if (_sFBoltFill != null) {
            return _sFBoltFill!!
        }
        _sFBoltFill = sfIcon(
            name = "Monochrome.SFBoltFill",
            viewportWidth = 17.6953f,
            viewportHeight = 27.9005f
        ) {
            addSfPath("M0 15.4395C0 15.8399 0.3125 16.1524 0.771484 16.1524L7.92969 16.1524L4.14062 26.4356C3.71094 27.5684 4.87305 28.1543 5.61523 27.2364L17.0117 12.9785C17.1973 12.7344 17.3047 12.5098 17.3047 12.2559C17.3047 11.8555 17.002 11.5332 16.543 11.5332L9.375 11.5332L13.1738 1.2598C13.6035 0.117218 12.4414-0.46872 11.6992 0.459015L0.302734 14.7168C0.117188 14.961 0 15.1856 0 15.4395Z", fillAlpha = 0.85f)
        }
        return _sFBoltFill!!
    }

private var _sFBoltFill: ImageVector? = null
