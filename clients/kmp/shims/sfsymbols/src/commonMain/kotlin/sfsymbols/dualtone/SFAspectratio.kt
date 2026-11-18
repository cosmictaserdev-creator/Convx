package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAspectratio (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFAspectratio: ImageVector
    get() {
        if (_sFAspectratio != null) {
            return _sFAspectratio!!
        }
        _sFAspectratio = sfIcon(
            name = "Dualtone.SFAspectratio",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.6562 22.1777L22.6562 9.99023C22.6562 8.01758 21.5723 6.94336 19.5703 6.94336L0.927734 6.94336L0.927734 8.49609L19.4629 8.49609C20.498 8.49609 21.1035 9.11133 21.1035 10.1562L21.1035 22.1777ZM15.9082 7.46094L14.3555 7.46094L14.3555 22.1777L15.9082 22.1777ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFAspectratio!!
    }

private var _sFAspectratio: ImageVector? = null
