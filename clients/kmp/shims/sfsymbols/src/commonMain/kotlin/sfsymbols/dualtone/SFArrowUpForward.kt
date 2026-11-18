package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpForward (dualtone)
 * Viewport: 18.8575 x 18.4668
 */
public val SfSymbols.Dualtone.SFArrowUpForward: ImageVector
    get() {
        if (_sFArrowUpForward != null) {
            return _sFArrowUpForward!!
        }
        _sFArrowUpForward = sfIcon(
            name = "Dualtone.SFArrowUpForward",
            viewportWidth = 18.8575f,
            viewportHeight = 18.4668f
        ) {
            addSfPath("M0.293015 18.1641C0.68364 18.5449 1.23051 18.5742 1.64067 18.1641L14.8926 4.92188L17.3438 2.30469C17.7149 1.94336 17.7051 1.45508 17.3633 1.10352C17.0118 0.761719 16.5235 0.751953 16.1622 1.11328L13.545 3.57422L0.293015 16.8262C-0.107376 17.2266-0.0878449 17.7734 0.293015 18.1641ZM16.5625 8.33008L16.5625 13.8867C16.5625 14.3945 16.9922 14.8535 17.5293 14.8535C18.0469 14.8535 18.4961 14.4238 18.4961 13.8477L18.4766 1.03516C18.4766 0.429688 18.0762 0 17.4512 0L4.62895 0C4.03325 0 3.63286 0.458984 3.63286 0.966797C3.63286 1.48438 4.08208 1.91406 4.58989 1.91406L9.65825 1.91406L16.7969 1.69922Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpForward!!
    }

private var _sFArrowUpForward: ImageVector? = null
