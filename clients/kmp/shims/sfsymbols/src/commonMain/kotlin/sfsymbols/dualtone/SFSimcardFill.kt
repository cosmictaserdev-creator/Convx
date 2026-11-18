package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSimcardFill (dualtone)
 * Viewport: 21.3281 x 26.9434
 */
public val SfSymbols.Dualtone.SFSimcardFill: ImageVector
    get() {
        if (_sFSimcardFill != null) {
            return _sFSimcardFill!!
        }
        _sFSimcardFill = sfIcon(
            name = "Dualtone.SFSimcardFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9434f
        ) {
            addSfPath("M3.75977 26.9434L17.207 26.9434C19.707 26.9434 20.9668 25.6543 20.9668 23.1445L20.9668 8.93555C20.9668 7.80273 20.7422 7.1875 20.0879 6.54297L14.5117 0.976562C13.8672 0.3125 13.1445 0.0292969 12.1094 0.0292969L3.75977 0.0292969C1.2793 0.0292969 0 1.30859 0 3.82812L0 23.1445C0 25.6641 1.25977 26.9434 3.75977 26.9434Z", fillAlpha = 0.2125f)
            addSfPath("M4.47266 21.0449L4.47266 16.3477L7.8418 16.3477C7.95898 16.3477 8.03711 16.416 8.03711 16.543L8.03711 22.8809L6.29883 22.8809C5.15625 22.8809 4.47266 22.1973 4.47266 21.0449ZM4.47266 15.1172L4.47266 13.5742C4.47266 12.4121 5.15625 11.7188 6.29883 11.7188L11.7383 11.7188L11.7383 22.8809L9.27734 22.8809L9.27734 15.8496C9.27734 15.3906 9.00391 15.1172 8.53516 15.1172ZM12.9785 22.8809L12.9785 11.7188L14.6875 11.7188C15.8301 11.7188 16.5137 12.4121 16.5137 13.5742L16.5137 21.0449C16.5137 22.1973 15.8301 22.8809 14.6875 22.8809Z", fillAlpha = 0.85f)
        }
        return _sFSimcardFill!!
    }

private var _sFSimcardFill: ImageVector? = null
