package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNosignAppFill (monochrome)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Monochrome.SFNosignAppFill: ImageVector
    get() {
        if (_sFNosignAppFill != null) {
            return _sFNosignAppFill!!
        }
        _sFNosignAppFill = sfIcon(
            name = "Monochrome.SFNosignAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M21.1523 1.74805C22.3145 2.90039 22.9102 4.60938 22.9102 6.81641L22.9102 16.084C22.9102 18.291 22.3047 20.0098 21.1523 21.1523C20.0293 22.2754 18.3105 22.9004 16.0938 22.9004L6.81641 22.9004C4.59961 22.9004 2.89062 22.2852 1.75781 21.1523C0.595703 20 0 18.291 0 16.084L0 6.81641C0 4.60938 0.605469 2.89062 1.75781 1.74805C2.88086 0.625 4.59961 0 6.81641 0L16.0938 0C18.3105 0 20.0195 0.605469 21.1523 1.74805ZM4.54102 11.4453C4.54102 15.2539 7.62695 18.3398 11.4453 18.3398C15.2539 18.3398 18.3496 15.2539 18.3496 11.4453C18.3496 7.62695 15.2539 4.54102 11.4453 4.54102C7.62695 4.54102 4.54102 7.62695 4.54102 11.4453ZM14.5117 15.7031C13.6621 16.3379 12.5977 16.709 11.4551 16.709C8.53516 16.709 6.18164 14.3555 6.18164 11.4453C6.18164 10.293 6.55273 9.23828 7.19727 8.37891ZM16.7285 11.4453C16.7285 12.5977 16.3477 13.6621 15.7031 14.5215L8.36914 7.1875C9.22852 6.55273 10.293 6.17188 11.4551 6.17188C14.3652 6.17188 16.7285 8.52539 16.7285 11.4453Z", fillAlpha = 0.85f)
        }
        return _sFNosignAppFill!!
    }

private var _sFNosignAppFill: ImageVector? = null
