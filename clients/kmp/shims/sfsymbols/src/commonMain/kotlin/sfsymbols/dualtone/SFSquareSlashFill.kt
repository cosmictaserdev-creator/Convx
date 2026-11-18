package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareSlashFill (dualtone)
 * Viewport: 28.2446 x 27.8687
 */
public val SfSymbols.Dualtone.SFSquareSlashFill: ImageVector
    get() {
        if (_sFSquareSlashFill != null) {
            return _sFSquareSlashFill!!
        }
        _sFSquareSlashFill = sfIcon(
            name = "Dualtone.SFSquareSlashFill",
            viewportWidth = 28.2446f,
            viewportHeight = 27.8687f
        ) {
            addSfPath("M22.2866 25.3765C22.0753 25.4026 21.8494 25.4139 21.6126 25.4139L6.26099 25.4139C3.74146 25.4139 2.46216 24.1541 2.46216 21.6541L2.46216 6.22441C2.46216 6.00619 2.47191 5.79735 2.49496 5.60136ZM25.4211 6.22441L25.4211 21.6541C25.4211 21.8716 25.4113 22.0798 25.3881 22.2753L5.60327 2.49052C5.80966 2.46554 6.03012 2.45488 6.26099 2.45488L21.6126 2.45488C24.1419 2.45488 25.4211 3.73418 25.4211 6.22441Z", fillAlpha = 0.425f)
            addSfPath("M25.0989 26.2635C25.4211 26.5857 25.9583 26.5857 26.2708 26.2635C26.6028 25.9314 26.6028 25.4139 26.2708 25.0818L2.78443 1.60527C2.47193 1.28301 1.93482 1.27324 1.60279 1.60527C1.28052 1.91777 1.28052 2.46465 1.60279 2.78691Z", fillAlpha = 0.85f)
        }
        return _sFSquareSlashFill!!
    }

private var _sFSquareSlashFill: ImageVector? = null
