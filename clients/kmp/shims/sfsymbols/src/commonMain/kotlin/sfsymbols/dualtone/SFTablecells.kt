package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTablecells (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFTablecells: ImageVector
    get() {
        if (_sFTablecells != null) {
            return _sFTablecells!!
        }
        _sFTablecells = sfIcon(
            name = "Dualtone.SFTablecells",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M0.517578 8.80859L29.1699 8.80859L29.1699 7.08008L0.517578 7.08008ZM0.517578 15.8887L29.1699 15.8887L29.1699 14.1602L0.517578 14.1602ZM13.9258 22.4414L15.6543 22.4414L15.6543 0.517578L13.9258 0.517578ZM3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFTablecells!!
    }

private var _sFTablecells: ImageVector? = null
