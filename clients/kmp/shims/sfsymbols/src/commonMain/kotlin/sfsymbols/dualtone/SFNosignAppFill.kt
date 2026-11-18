package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNosignAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFNosignAppFill: ImageVector
    get() {
        if (_sFNosignAppFill != null) {
            return _sFNosignAppFill!!
        }
        _sFNosignAppFill = sfIcon(
            name = "Dualtone.SFNosignAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M11.4453 18.3398C7.62695 18.3398 4.54102 15.2539 4.54102 11.4453C4.54102 7.62695 7.62695 4.54102 11.4453 4.54102C15.2539 4.54102 18.3496 7.62695 18.3496 11.4453C18.3496 15.2539 15.2539 18.3398 11.4453 18.3398ZM6.18164 11.4453C6.18164 14.3555 8.53516 16.709 11.4551 16.709C12.5977 16.709 13.6621 16.3379 14.5117 15.7031L7.19727 8.37891C6.55273 9.23828 6.18164 10.293 6.18164 11.4453ZM8.36914 7.1875L15.7031 14.5215C16.3477 13.6621 16.7285 12.5977 16.7285 11.4453C16.7285 8.52539 14.3652 6.17188 11.4551 6.17188C10.293 6.17188 9.22852 6.55273 8.36914 7.1875Z", fillAlpha = 0.85f)
        }
        return _sFNosignAppFill!!
    }

private var _sFNosignAppFill: ImageVector? = null
