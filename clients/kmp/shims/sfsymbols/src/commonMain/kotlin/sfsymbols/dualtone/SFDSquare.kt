package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDSquare: ImageVector
    get() {
        if (_sFDSquare != null) {
            return _sFDSquare!!
        }
        _sFDSquare = sfIcon(
            name = "Dualtone.SFDSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M8.11523 17.0898L11.3672 17.0898C14.8438 17.0898 16.8457 15.0488 16.8457 11.3477C16.8457 7.73438 14.8633 5.68359 11.3672 5.68359L8.11523 5.68359C7.56836 5.68359 7.27539 6.07422 7.27539 6.65039L7.27539 16.1133C7.27539 16.6895 7.56836 17.0898 8.11523 17.0898ZM8.95508 15.7227L8.95508 7.05078L11.1621 7.05078C13.6621 7.05078 15.0977 8.58398 15.0977 11.377C15.0977 14.2578 13.6719 15.7227 11.1621 15.7227Z", fillAlpha = 0.85f)
        }
        return _sFDSquare!!
    }

private var _sFDSquare: ImageVector? = null
