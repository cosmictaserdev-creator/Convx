package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneUpForward (dualtone)
 * Viewport: 24.7504 x 24.848
 */
public val SfSymbols.Dualtone.SFAirplaneUpForward: ImageVector
    get() {
        if (_sFAirplaneUpForward != null) {
            return _sFAirplaneUpForward!!
        }
        _sFAirplaneUpForward = sfIcon(
            name = "Dualtone.SFAirplaneUpForward",
            viewportWidth = 24.7504f,
            viewportHeight = 24.848f
        ) {
            addSfPath("M24.0207 0.827331C23.0051-0.178528 20.8762 0.466003 19.216 2.11639L15.6613 5.68085C15.1828 6.1496 14.9191 6.25702 14.3039 6.22772L2.11639 5.69061C1.716 5.68085 1.38397 5.7785 1.13983 6.03241L0.133972 7.0578C-0.100403 7.27264-0.0222781 7.61444 0.329284 7.73163L9.96796 11.3742L5.96405 16.3742L1.51093 15.0656C1.17889 14.9777 0.905456 15.0656 0.632019 15.3488L0.241394 15.7297C0.0167844 15.9641 0.00701876 16.2277 0.241394 16.4621L8.38593 24.6066C8.6203 24.841 8.89374 24.8312 9.11835 24.6066L9.50897 24.216C9.78241 23.9426 9.8703 23.6691 9.78241 23.3469L8.47382 18.8937L13.4836 14.8801L17.1164 24.5187C17.2336 24.8703 17.5754 24.9484 17.8 24.7141L18.8156 23.7082C19.0695 23.4641 19.1769 23.132 19.1672 22.7316L18.6203 10.5441C18.591 9.92889 18.6984 9.66522 19.1769 9.18671L22.7316 5.63202C24.382 3.97186 25.0266 1.84296 24.0207 0.827331Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneUpForward!!
    }

private var _sFAirplaneUpForward: ImageVector? = null
