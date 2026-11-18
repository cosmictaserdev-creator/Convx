package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpLeft (dualtone)
 * Viewport: 18.8379 x 18.4668
 */
public val SfSymbols.Dualtone.SFArrowUpLeft: ImageVector
    get() {
        if (_sFArrowUpLeft != null) {
            return _sFArrowUpLeft!!
        }
        _sFArrowUpLeft = sfIcon(
            name = "Dualtone.SFArrowUpLeft",
            viewportWidth = 18.8379f,
            viewportHeight = 18.4668f
        ) {
            addSfPath("M18.2031 18.1641C18.5938 17.7734 18.6035 17.2266 18.2031 16.8262L4.96094 3.57422L2.34375 1.11328C1.98242 0.751953 1.48438 0.761719 1.14258 1.10352C0.800781 1.45508 0.78125 1.94336 1.15234 2.30469L3.61328 4.92188L16.8555 18.1641C17.2656 18.5742 17.8125 18.5449 18.2031 18.1641ZM1.93359 8.33008L1.70898 1.69922L8.83789 1.91406L13.916 1.91406C14.4238 1.91406 14.873 1.48438 14.873 0.966797C14.873 0.458984 14.4727 0 13.8672 0L1.05469 0C0.419922 0 0.0195312 0.429688 0.0195312 1.03516L0 13.8477C0 14.4238 0.458984 14.8535 0.976562 14.8535C1.50391 14.8535 1.93359 14.3945 1.93359 13.8867Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpLeft!!
    }

private var _sFArrowUpLeft: ImageVector? = null
