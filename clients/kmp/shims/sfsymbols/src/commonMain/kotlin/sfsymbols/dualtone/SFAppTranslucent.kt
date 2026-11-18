package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppTranslucent (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFAppTranslucent: ImageVector
    get() {
        if (_sFAppTranslucent != null) {
            return _sFAppTranslucent!!
        }
        _sFAppTranslucent = sfIcon(
            name = "Dualtone.SFAppTranslucent",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M0 10.127L22.9102 10.127L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641ZM0 15.8301L22.9102 15.8301L22.9102 13.3789L0 13.3789ZM0.927734 20.0879L21.9727 20.0879C22.2559 19.5996 22.4707 19.0527 22.627 18.4375L0.273438 18.4375C0.419922 19.0527 0.644531 19.5996 0.927734 20.0879ZM6.81641 22.9004L16.0938 22.9004C17.6172 22.9004 18.9062 22.6074 19.9219 22.0508L2.97852 22.0508C3.99414 22.6074 5.29297 22.9004 6.81641 22.9004Z", fillAlpha = 0.85f)
        }
        return _sFAppTranslucent!!
    }

private var _sFAppTranslucent: ImageVector? = null
