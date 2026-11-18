package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillsFill (dualtone)
 * Viewport: 29.7055 x 25.6543
 */
public val SfSymbols.Dualtone.SFPillsFill: ImageVector
    get() {
        if (_sFPillsFill != null) {
            return _sFPillsFill!!
        }
        _sFPillsFill = sfIcon(
            name = "Dualtone.SFPillsFill",
            viewportWidth = 29.7055f,
            viewportHeight = 25.6543f
        ) {
            addSfPath("M29.3449 18.3398C28.9738 15.127 26.2199 12.627 22.8899 12.627C19.5695 12.627 16.8156 15.127 16.4445 18.3398ZM29.3449 19.9219L16.4445 19.9219C16.8254 23.1445 19.5598 25.6543 22.8899 25.6543C26.2395 25.6543 28.9738 23.1543 29.3449 19.9219Z", fillAlpha = 0.425f)
            addSfPath("M13.1731 14.8242L5.81954 7.4707L1.74727 11.543C-0.547649 13.8379-0.576946 16.8066 1.63009 19.0039C3.86641 21.2207 6.81563 21.1816 9.10079 18.8965Z", fillAlpha = 0.85f)
            addSfPath("M14.3156 13.6719L18.3879 9.60938C20.6828 7.31445 20.7121 4.3457 18.4953 2.14844C16.2688-0.0683594 13.3195-0.0292969 11.0344 2.25586L6.97188 6.32812Z", fillAlpha = 0.85f)
        }
        return _sFPillsFill!!
    }

private var _sFPillsFill: ImageVector? = null
